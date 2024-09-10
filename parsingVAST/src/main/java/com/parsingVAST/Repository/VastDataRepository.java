package com.parsingVAST.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.parsingVAST.Model.VastData;

@Repository
public interface VastDataRepository extends MongoRepository<VastData, String> {
    // Additional query methods can be added here if needed
}
