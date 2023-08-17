package com.igd.bc.service;

import com.igd.bc.model.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.stream.Collectors;

import static com.igd.bc.constants.BlockChainApiConstants.*;

@Service
public class BlockChainApiService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value(value = "${bc.graphiQL.query}")
    private String graphiQLquery;

    @Value(value = "${bc.graphiQL.url}")
    private String graphiQLurl;

    @Autowired
    private RestTemplate template;

    public ResponseEntity<APIGQLResponse> getGraphiQLData(String from, String to, String dateFrom, String orderBy){
        logger.info("GraphiQL Query == "+ graphiQLquery);
        String graphiQLFormattedquery = String.format(graphiQLquery, constructFilter(from, to, dateFrom, orderBy));
        GraphiQLResponse response = callGraphiQL(new GraphiQLRequest(graphiQLFormattedquery, null, new Extensions()));
        if(ObjectUtils.isNotEmpty(response.getData())){
            APIGQLResponse apiResponse = graphQLToAPIMapper(response);
            logger.info("Final Response Sent");
            return new ResponseEntity<APIGQLResponse>(apiResponse, HttpStatus.OK);
        }
        logger.info("Final Response Sent");
        return new ResponseEntity<APIGQLResponse>(new APIGQLResponse(), HttpStatus.NOT_FOUND);
    }

    public String constructFilter(String from, String to,String dateFrom, String orderBy){
        logger.info("Inside constructFilter");
        StringBuilder filter = new StringBuilder("");
        boolean isWhere = false;
        if(StringUtils.isNotEmpty(from) || StringUtils.isNotEmpty(to) || StringUtils.isNotEmpty(dateFrom)){
            isWhere = true;
            filter.append("(where: {");
            if(StringUtils.isNotEmpty(from)) filter.append("from: " + DQ_ESC + from + DQ_ESC);
            if(StringUtils.isNotEmpty(to)) filter.append(", to: " + DQ_ESC + to + DQ_ESC);
            if(StringUtils.isNotEmpty(dateFrom)) filter.append(", blockTimestamp_gt: " + DQ_ESC + dateFrom + DQ_ESC);
        }
        if(StringUtils.isNotEmpty(orderBy)){
            if(isWhere) filter.append("} orderBy: " + orderBy + ")");
            else filter.append("(orderBy: " + orderBy + ")");
        }
        else{
            if(isWhere) filter.append(")");
        }
        logger.info("Exiting constructFilter");
        return filter.toString();
    }

    public GraphiQLResponse callGraphiQL(GraphiQLRequest request){
        logger.info("GraphiQL Request ::: " + request);
        try{
            ResponseEntity<GraphiQLResponse> responseEntity
                    = template.postForEntity(graphiQLurl, request, GraphiQLResponse.class);
            try{
                logger.info("GraphiQL Response Object instance Count :::: "
                        + responseEntity.getBody().getData().getTransfers().size());
            }
            catch(Exception ex){
                logger.info("GraphiQL Response Object instance Count :::: 0");
            }
            return responseEntity.getBody();
        }
        catch(Exception ex) {
            logger.error("Error occured while calling GraphiQL API :::: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    public APIGQLResponse graphQLToAPIMapper(GraphiQLResponse gQLResponse){
        APIGQLResponse apigqlResponse = new APIGQLResponse();
        apigqlResponse.setData(gQLResponse.getData().
                        getTransfers().stream().map(transfer -> new Transfer(
                                transfer.getId(),
                                transfer.getFrom(),
                                transfer.getTo(),
                                transfer.getValue(),
                                convertEpochToDT(transfer.getBlockTimestamp())
                        )).collect(Collectors.toList())
        );
        return apigqlResponse;
    }

    public String convertEpochToDT(String epoch){
        return LocalDateTime.ofEpochSecond(Long.parseLong(epoch),0, ZoneOffset.UTC).toString();
        //return ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
