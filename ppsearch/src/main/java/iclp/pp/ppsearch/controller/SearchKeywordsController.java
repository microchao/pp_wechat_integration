package iclp.pp.ppsearch.controller;

import com.google.gson.Gson;
import iclp.pp.ppsearch.model.LoungeSearchModel;
import iclp.pp.ppsearch.model.RequestJsonModel;
import iclp.pp.ppsearch.model.ResponseXSocialModel;
import iclp.pp.ppsearch.model.SearchLogModel;
import iclp.pp.ppsearch.service.repository.SearchLogRepository;
import iclp.pp.ppsearch.util.JsonUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@Scope("request")
public class SearchKeywordsController {

    private static CloseableHttpClient httpClient;

    private String toUserName;

    private String FromUserName;

    private Integer articleCount;

    private Logger logger;

    private StringBuffer stringBuffer;

    private List<LoungeSearchModel> allLougeSearchModelList;

    private  String keyword;

    @Autowired
    private SearchLogRepository searchLogRepository;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(value="/txtsearch",method =  { RequestMethod.GET, RequestMethod.POST })
    public String search(@RequestBody String jsonString,
                     HttpServletRequest request) {

        Gson gson = JsonUtil.instanceOfGson();
        RequestJsonModel requestJsonModel = gson.fromJson(jsonString,RequestJsonModel.class);

        if(requestJsonModel.getEchostr() != null) {
            return requestJsonModel.getEchostr();
        }
        stringBuffer = new StringBuffer();
        allLougeSearchModelList = new ArrayList<>();
        logger = LoggerFactory.getLogger(SearchKeywordsController.class);
//        Map map = XmlUtil.parseXml(request);
        keyword = requestJsonModel.getContent();
        logger.info("openid=" + requestJsonModel.getOpenid() + " 搜索：" + keyword + " 开始");
        this.toUserName = requestJsonModel.getOpenid();
        this.FromUserName = requestJsonModel.getToUserName();
        LoungeSearchModel redisLoungeModel = getByRedis(keyword);
        SearchLogModel searchLogModel = new SearchLogModel();
        searchLogModel.setKeyword(keyword);
        searchLogModel.setOpenid(requestJsonModel.getOpenid());
        searchLogModel.setSource(requestJsonModel.getSource());
        String xml = null;
        String code = null;
        if(redisLoungeModel != null) {
            long redisStartTime = System.currentTimeMillis();
            articleCount = redisLoungeModel.getArticleCount();
            sortLoungeSearchModel(redisLoungeModel);
            xml = getLounghNewsXml();
            long redisEndTime = System.currentTimeMillis();
            logger.info("redis此次" + keyword + "请求花费" + (redisEndTime - redisStartTime) + "/ms");
            saveToMysql(searchLogModel);
            code = "200";
        }
        else{
            LoungeSearchModel loungeSearchModel = convertToLoungeSearchModel(keyword);
            if(loungeSearchModel.getResults().get(0).getItemId().equals("00000000-0000-0000-0000-000000000000")) {
                code = "404";
                ResponseXSocialModel responseXSocialModel = new ResponseXSocialModel();
                responseXSocialModel.setCode(code);
                responseXSocialModel.setXml(getNoResult());
                return gson.toJson(responseXSocialModel);
            }
            sortLoungeSearchModel(loungeSearchModel);
            // 插入redis
            saveToRedis(keyword,loungeSearchModel);
            code = "200";
            xml = getLounghNewsXml();
            saveToMysql(searchLogModel);
        }
        if(code == null) {
            code = "500";
        }
        ResponseXSocialModel responseXSocialModel = new ResponseXSocialModel();
        responseXSocialModel.setCode(code);
        responseXSocialModel.setXml(xml);
        logger.info("openid=" + requestJsonModel.getOpenid() + " 搜索：" + keyword + " 结束");
        return gson.toJson(responseXSocialModel);
    }

    private void saveToMysql(SearchLogModel searchLog) {
        searchLogRepository.save(searchLog);
        logger.info("存mysql=" + searchLog + "成功");
    }

    private void saveToRedis(String keyword,LoungeSearchModel loungeSearchModel) {
        Gson gson = JsonUtil.instanceOfGson();
        String json = gson.toJson(loungeSearchModel,LoungeSearchModel.class);
        redisTemplate.opsForValue().set(keyword, json,1,TimeUnit.DAYS);
        logger.info("存redis key=" + keyword + ", value=" + json + "成功");
    }

    private LoungeSearchModel getByRedis(String keyword) {
        Gson gson  = JsonUtil.instanceOfGson();
        Object object = redisTemplate.opsForValue().get(keyword);
        if(object != null) {
            String json = object.toString();
//            articleCount = getAricleCount(json);
            logger.info("获取redis key=" + keyword + ", value=" + json + "成功");
            return gson.fromJson(json,LoungeSearchModel.class);
        }
        return null;
    }

    @RequestMapping(value="/test",method =  { RequestMethod.GET, RequestMethod.POST })
    public String test() {
        System.out.println(redisTemplate.opsForValue().get("test"));
        return "test";
    }

    /**
     * 没有查询结果返回的xml
     * @return
     */
    private String getNoResult() {
        String xml = "<xml> " +
                "       <ToUserName>"  + toUserName + "</ToUserName> " +
                "       <FromUserName>" + FromUserName +"</FromUserName> " +
                "       <CreateTime>12345678</CreateTime> " +
                "       <MsgType>text</MsgType> " +
                "       <Content>没有相关搜寻结果</Content> " +
                "     </xml>";
        return xml;
    }

    private void sortLoungeSearchModel(LoungeSearchModel tempModel) {
        addLoungeModel(tempModel.getResults());
        List<LoungeSearchModel> result  = new ArrayList<>();
        LoungeSearchModel topOne = null;
        for(LoungeSearchModel model : allLougeSearchModelList) {
            if(model.getCode() == null) {
               result.add(0,model);
               continue;
            }
            if(model.getCode() != null && model.getCode().toLowerCase().equals(keyword.toLowerCase())) {
                topOne = model;
            }
            result.add(model);
        }
        if(topOne != null) {
            result.remove(topOne);
            result.add(0,topOne);
        }
        allLougeSearchModelList = result;
    }

    /**
     * 返回关键字查询结果
     * @return
     */
    private String getLounghNewsXml() {
        stringBuffer.append(
                "<xml>" +
                    "<ToUserName>" + toUserName + "</ToUserName>" +
                    "<FromUserName>" + FromUserName + "</FromUserName>" +
                    "<CreateTime>12345678</CreateTime>" +
                    "<MsgType>news</MsgType>" +
                    "<ArticleCount></ArticleCount>" +
                    "<Articles>");
        appendChildItemXml();
        stringBuffer.append("</Articles>" +
                "</xml>");
        String xml = stringBuffer.toString().replace("<ArticleCount></ArticleCount>","<ArticleCount>" + articleCount +"</ArticleCount>");
        return xml;
    }

    /**
     * 递归添加children
     */
    private void appendChildItemXml() {
        int limit = 0;
        for (LoungeSearchModel loungeSearchModelEntity : allLougeSearchModelList) {
            limit++;
            String picUrl = "https://d10mzz35brm2m8.cloudfront.net/Global/Logos/logo-rounded-7d731234-66ec-45eb-9134-7fdd1b29361b.png?h=46&la=zh-CN&w=46";
            if (limit == 1) {
                picUrl = "https://mmbiz.qpic.cn/mmbiz_png/ZAtZqscXMmVVDYKxJ9TO13YGzy85TWMv9jyt3J7JJpN4IWSh6N0Khl9QTBXSkjibsh1TD0UbQlKy3MvY8RMjTVQ/0?wx_fmt=png";
            }
            stringBuffer.append("<item>" +
                    "  <Title>" + loungeSearchModelEntity.getName() + "</Title> " +
                    "  <Description>" + loungeSearchModelEntity.getCode() + "</Description>" +
                    "  <PicUrl>" + picUrl + "</PicUrl>" +
                    "  <Url>" + loungeSearchModelEntity.getUrl() + "</Url>" +
                    "</item>");
            if (limit == 8) break;
        }
    }

    /**
     * 递归获取所有model
     * @param loungeSearchModelList
     */
    private void addLoungeModel( List<LoungeSearchModel> loungeSearchModelList) {
        for (LoungeSearchModel loungeSearchModelEntity : loungeSearchModelList) {
            allLougeSearchModelList.add(loungeSearchModelEntity);
            if (loungeSearchModelEntity.getChildren().size() > 0) {
                addLoungeModel(loungeSearchModelEntity.getChildren());
            }
        }
    }

    private LoungeSearchModel convertToLoungeSearchModel(String keyword) {
        long startTime = System.currentTimeMillis();
        httpClient = HttpClients.createDefault();
        keyword = keyword.trim();
        String url = "https://www.prioritypass.com/api/search/loungesearch?keyword="  + keyword + "&language=zh-CN";
        url = url.replaceAll(" ","%20");

        HttpGet httpGet = new HttpGet(url);
        LoungeSearchModel loungeSearchModel = null;
        CloseableHttpResponse closeableHttpResponse = null;
        try {
            closeableHttpResponse = httpClient.execute(httpGet);
            String body = EntityUtils.toString(closeableHttpResponse.getEntity());
            articleCount = getAricleCount(body);
            Gson gson = new Gson();
            loungeSearchModel = gson.fromJson(body, LoungeSearchModel.class);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try{
                closeableHttpResponse.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        long endTime = System.currentTimeMillis();
        logger.info("article Count = " + articleCount);
        logger.info("此次" + keyword + "请求花费" + (endTime - startTime) + "/ms");
        loungeSearchModel.setArticleCount(articleCount);
        return loungeSearchModel;
    }

    private int getAricleCount(String str) {
        int count = StringUtils.countMatches(str,"Code");
        if(count >= 8 ){
            return 8;
        }
        return count;
    }
}
