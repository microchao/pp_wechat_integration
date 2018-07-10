package iclp.pp.ppsearch.model;

import lombok.Data;

import java.util.List;

@Data
public class LoungeSearchModel {

    private String Code;

    private String ItemId;

    private String Name;

    private String Parent;

    private String Url;

    private List<LoungeSearchModel> Children;

    private List<LoungeSearchModel> Results;

}
