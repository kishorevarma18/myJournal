package com.jkv.myjournal.repository.impl;

import java.util.ArrayList;
import java.util.List;


import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.jkv.myjournal.entity.UserEntity;
import com.jkv.myjournal.repository.CustomUserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository{

    private final MongoTemplate mongoTemplate;

    @Override
    public List<UserEntity> findUsersByCityAndRole(String role, String city) {
        Query query =new Query();
        // query is the starting point to add the criteria to it and send it to find() in mongoTemplate so fetch the data based on criteria in it.
        List<Criteria> criteriaList = new ArrayList<>();
        if(role!=null&&!role.isEmpty()){
            criteriaList.add(Criteria.where("roles").regex(role,"i"));
            //here we are just mentioning the criteria we are not adding in the Query yet.
        }
        if(city!=null&&!city.isEmpty()){
            String location = city.replaceAll("[^a-zA-Z]","");
            if(!location.isEmpty()){
                String regexPattern = ".*"+java.util.regex.Pattern.quote(city)+".*";
                /*
                java.util.regex.Pattern.quote(city) is used to ensure that the user's input is treated strictly as literal text, rather than executable regular expression code.
                Without it, any special characters typed into your search box will alter your database query logic or crash your application.
                The Problem: Regular Expression Metacharacters
                In regular expressions, certain characters have built-in operational powers:
                . means "match any single character".
                ( and ) are used to start and end capture groups.
                * and + mean "match the previous token X times".
                | means a logical OR.
                */
                criteriaList.add(Criteria.where("city").regex(regexPattern,"i"));
            }
            else{
                criteriaList.add(Criteria.where("city").is("____FAKE__CITY__NAME___"));
            }   
        }
        if (!criteriaList.isEmpty()) {
            Criteria[] criteriaArray = criteriaList.stream().toArray(Criteria[]::new);
            //in the below process we are adding criteria to the query.
            query.addCriteria(new Criteria().andOperator(criteriaArray));
            /*
            You do need new Criteria().andOperator(...) or new Criteria().orOperator(...).
            This is because these functions combine multiple whole criteria statements together under a logical boundary.
            However, for operators like gt, lt, gte, lte, you do NOT use new Criteria().
            They attach directly to a specific field key, just like .is() or .regex().
            Example: Criteria.where("age").gte(18) (No new Criteria() needed here).
            */
        }
        /*
        we can add criteria to the query directly without any operator in between the criterias like below examples.
        query.addCriteria(Criteria.where("city").is(city));
        query.addCriteria(Criteria.where("roles").is(role));
        */
        return mongoTemplate.find(query, UserEntity.class);
    }

}
