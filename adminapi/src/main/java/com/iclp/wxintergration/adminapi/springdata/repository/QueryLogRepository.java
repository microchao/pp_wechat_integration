package com.iclp.wxintergration.adminapi.springdata.repository;

import com.iclp.wxintergration.adminapi.model.SearchLogModel;
import org.springframework.data.repository.CrudRepository;

public interface QueryLogRepository extends CrudRepository<SearchLogModel,Integer> {


}

