package casia.isi.elasticsearch.monitor.alarm;
/**
 * 　　　　　　　 ┏┓       ┏┓+ +
 * 　　　　　　　┏┛┻━━━━━━━┛┻┓ + +
 * 　　　　　　　┃　　　　　　 ┃
 * 　　　　　　　┃　　　━　　　┃ ++ + + +
 * 　　　　　　 █████━█████  ┃+
 * 　　　　　　　┃　　　　　　 ┃ +
 * 　　　　　　　┃　　　┻　　　┃
 * 　　　　　　　┃　　　　　　 ┃ + +
 * 　　　　　　　┗━━┓　　　 ┏━┛
 * ┃　　  ┃
 * 　　　　　　　　　┃　　  ┃ + + + +
 * 　　　　　　　　　┃　　　┃　Code is far away from     bug with the animal protecting
 * 　　　　　　　　　┃　　　┃ +
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃　　+
 * 　　　　　　　　　┃　 　 ┗━━━┓ + +
 * 　　　　　　　　　┃ 　　　　　┣┓
 * 　　　　　　　　　┃ 　　　　　┏┛
 * 　　　　　　　　　┗┓┓┏━━━┳┓┏┛ + + + +
 * 　　　　　　　　　 ┃┫┫　 ┃┫┫
 * 　　　　　　　　　 ┗┻┛　 ┗┻┛+ + + +
 */

import casia.isi.elasticsearch.monitor.common.EsUrl;
import casia.isi.elasticsearch.monitor.common.SysConstant;
import casia.isi.elasticsearch.operation.http.HttpPoolSym;
import casia.isi.elasticsearch.operation.http.HttpProxyRegister;
import casia.isi.elasticsearch.operation.http.HttpProxyRequest;
import casia.isi.elasticsearch.operation.search.EsIndexSearch;
import casia.isi.elasticsearch.util.ClientUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author YanchaoMa yanchaoma@foxmail.com
 * @PACKAGE_NAME: casia.isi.elasticsearch.monitor.alarm
 * @Description: TODO(Elasticsearch STATUS)
 * @date 2019/11/15 16:32
 */
public class ElasticStatistics {

    public HttpProxyRequest request = new HttpProxyRequest(HttpPoolSym.DEFAULT.getSymbolValue());

    private EsIndexSearch esIndexSearch;

    public ElasticStatistics() {
        HttpProxyRegister.register(SysConstant.ELASTICSEARCH_ADDRESS);
    }

    /**
     * @param
     * @return
     * @Description: TODO(集群整体情况)
     */
    public String statistics() {
        String queryResult = request.httpGet(ClientUtils.referenceUrl(EsUrl._cluster_state_all.url()));
        return queryResult;
    }

    /**
     * @param
     * @return
     * @Description: TODO(所有索引 - 索引类型和索引名)
     */
    public JSONArray recentThreeDaysIndexStatus() {
        JSONArray array = new JSONArray();
        JSONObject mapping = getAllIndices();
        for (Map.Entry entry : mapping.entrySet()) {
            String indexName = String.valueOf(entry.getKey());
            String indexType = String.valueOf(entry.getValue());
            esIndexSearch = new EsIndexSearch(SysConstant.ELASTICSEARCH_ADDRESS, indexName, indexType);

            esIndexSearch.addRangeTerms("pubtime", "2019-11-10 00:00:00", "2019-11-17 00:00:00");
            List<String[]> result = esIndexSearch.facetDate("pubtime", "yyyy-MM-dd", "1d");

//            List<String[]> result = esAllIndexSearch.facetDateBySecondFieldValueCount("index_time", "yyyy-MM-dd", "1d", "it");
            // OUTPUT
            int i = 0;
            for (String[] infos : result) {
                JSONObject object = new JSONObject();
                object.put("date", infos[0]);
                object.put("count", infos[1]);
                array.add(object);
//                System.out.print(i++ + ":");
//
//                int size = 0;
//                for (String info : infos) {
//                    size++;
//                    if (itMap.get(info) != null) {
//                        System.out.print(info + "-" + itMap.get(info) + "\t");
//                    } else {
//                        if (size == 2) {
//                            System.out.print("数据总量：" + info + "\t");
//                        } else {
//                            System.out.print(info + "\t");
//                        }
//                    }
//                }
//                System.out.println("");
            }
            esIndexSearch.reset();

        }
        return array;
    }

    /**
     * @param
     * @return
     * @Description: TODO(所有索引 - 索引类型和索引名)
     */
    public JSONObject getAllIndices() {
        String queryResult = request.httpGet(ClientUtils.referenceUrl(EsUrl._mapping.url()));
        JSONObject object = JSONObject.parseObject(queryResult);
        JSONObject mapping = new JSONObject();
        for (String indexName : object.keySet()) {
            JSONObject map = object.getJSONObject(indexName).getJSONObject("mappings");
            String indexType = map.keySet().stream().findFirst().get();
            mapping.put(indexName, indexType);
        }
        return mapping;
    }
}


