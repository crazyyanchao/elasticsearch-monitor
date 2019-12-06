package casia.isi.elasticsearch.monitor.common;
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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * @author YanchaoMa yanchaoma@foxmail.com
 * @PACKAGE_NAME: casia.isi.elasticsearch.monitor.common
 * @Description: TODO(Message)
 * @date 2019/11/15 16:24
 */
public class Message {

    private JSONObject object = new JSONObject();

    public Message setStatus(boolean bool) {
        this.object.put("status", bool);
        return this;
    }

    public Message putResult(JSONObject result) {
        this.object.put("result", result);
        this.object.put("count", result.size());
        return this;
    }

    public Message putResult(Map map) {
        JSONObject result = JSONObject.parseObject(JSON.toJSONString(map));
        this.object.put("result", result);
        this.object.put("count", result.size());
        return this;
    }

    public Message putResult(String result) {
        this.object.put("result", result);
        this.object.put("length", result.length());
        return this;
    }

    public Message putResult(JSONArray result) {
        this.object.put("result", result);
        this.object.put("count", result.size());
        return this;
    }

    public String toJSONString() {
        return this.object.toJSONString();
    }

}

