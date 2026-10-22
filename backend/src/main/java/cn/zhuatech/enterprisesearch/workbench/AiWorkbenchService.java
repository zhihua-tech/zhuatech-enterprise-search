/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.enterprisesearch.workbench;
import org.springframework.stereotype.Service;
import java.math.*;
import java.util.*;
import static cn.zhuatech.enterprisesearch.workbench.WorkbenchInput.*;
/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
@Service
public class AiWorkbenchService {
    /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
    public Map<String,Object> execute(Map<String,Object> r){
        validate(r);

        keys(r,"topK","results");int top=integer(r,"topK",1,100);Map<String,Double> scores=new TreeMap<>();Map<String,Set<String>> sources=new TreeMap<>();
        for(var row:rows(r,"results",0,200)){keys(row,"url","source","score");String raw=text(row,"url",2000),source=id(row,"source");double score=decimal(row,"score",0,1).doubleValue();java.net.URI uri;try{uri=java.net.URI.create(raw);}catch(IllegalArgumentException e){throw new IllegalArgumentException("资源地址格式无效");}
            String scheme=uri.getScheme()==null?"":uri.getScheme().toLowerCase(Locale.ROOT),host=uri.getHost();if(!Set.of("http","https").contains(scheme)||host==null||uri.getUserInfo()!=null||uri.getPort()>65535)throw new IllegalArgumentException("仅支持无凭据的HTTP(S)地址");
            host=host.toLowerCase(Locale.ROOT);if(host.contains(":")&&!host.startsWith("["))host="["+host+"]";int port=uri.getPort();String suffix=port==-1||scheme.equals("https")&&port==443||scheme.equals("http")&&port==80?"":":"+port;String path=uri.getRawPath()==null||uri.getRawPath().isEmpty()?"/":uri.getRawPath();String canonical=scheme+"://"+host+suffix+path+(uri.getRawQuery()==null?"":"?"+uri.getRawQuery());
            scores.merge(canonical,score,Math::max);sources.computeIfAbsent(canonical,x->new TreeSet<>()).add(source);
        }
        var result=scores.entrySet().stream().sorted(Map.Entry.<String,Double>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey())).limit(top).map(e->Map.<String,Object>of("url",e.getKey(),"score",round(e.getValue()),"sources",List.copyOf(sources.get(e.getKey())))).toList();return Map.of("results",result,"uniqueResources",scores.size(),"fetchesRemoteUrls",false);

    }
}
