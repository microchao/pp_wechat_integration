package iclp.pp.ppsearch.controller;

import com.google.gson.Gson;
import iclp.pp.ppsearch.model.LoungeSearchModel;
import iclp.pp.ppsearch.util.XmlUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@Scope("request")
public class SearchKeywordsController {

    private static CloseableHttpClient httpClient;

    private String toUserName;

    private String FromUserName;

    private Integer articleCount;

    private Logger logger;

    private StringBuffer stringBuffer;

    private LoungeSearchModel topLoungeSearchModel;

    private List<LoungeSearchModel> allLougeSearchModelList;

    private  String keyword;

    @RequestMapping(value="/search",method =  { RequestMethod.GET, RequestMethod.POST })
    public String search(@RequestParam(value = "name" ,required = false) String name ,
                         @RequestParam(value = "signature",required = false) String signature,
                         @RequestParam(value = "timestamp",required = false) String timestamp,
                         @RequestParam(value = "nonce",required = false) String nonce,
                         @RequestParam(value = "openid",required = false) String openid,
                         @RequestParam(value = "echostr",required = false) String echostr,
                         HttpServletRequest request) {
        if(echostr != null) {
            return echostr;
        }
        stringBuffer = new StringBuffer();
        allLougeSearchModelList = new ArrayList<>();
        logger = LoggerFactory.getLogger(SearchKeywordsController.class);
        Map map = XmlUtil.parseXml(request);
        keyword = map.get("Content").toString();
        logger.info("openid=" + openid + " 搜索：" + keyword + " 开始");
        this.toUserName = openid;
        this.FromUserName = map.get("ToUserName").toString();

        LoungeSearchModel loungeSearchModel = convertToLoungeSearchModel(keyword);
        if(loungeSearchModel.getResults().get(0).getItemId().equals("00000000-0000-0000-0000-000000000000")) {
            return getNoResult();
        }
        sortLoungeSearchModel(loungeSearchModel);
        String lounghNewsXml = getLounghNewsXml(loungeSearchModel);
        logger.info("openid=" + openid + " 搜索：" + keyword + " 结束");
        return lounghNewsXml;
    }

    @RequestMapping(value="/test",method =  { RequestMethod.GET, RequestMethod.POST })
    public String test() {
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
     * @param loungeSearchModel
     * @return
     */
    private String getLounghNewsXml(LoungeSearchModel loungeSearchModel) {
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
                picUrl = "https://d10mzz35brm2m8.cloudfront.net/Global/Logos/logo-footer-f5552661-a02c-4aae-afac-4cd7d17c3246.png?h=101&la=zh-CN&w=236";
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
        try {
            CloseableHttpResponse closeableHttpResponse = httpClient.execute(httpGet);
            String body = EntityUtils.toString(closeableHttpResponse.getEntity());
            articleCount = getAricleCount(body);
            Gson gson = new Gson();
            loungeSearchModel = gson.fromJson(body, LoungeSearchModel.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        logger.info("此次" + keyword + "请求花费" + (endTime - startTime) + "/ms");
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
