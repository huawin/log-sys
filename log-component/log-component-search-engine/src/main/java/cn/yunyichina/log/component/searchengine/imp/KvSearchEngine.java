package cn.yunyichina.log.component.searchengine.imp;

import cn.yunyichina.log.common.entity.dto.SearchConditionDTO;
import cn.yunyichina.log.component.index.entity.ContextInfo;
import cn.yunyichina.log.component.index.entity.KvIndex;
import cn.yunyichina.log.component.searchengine.AbstractSearchEngine;
import cn.yunyichina.log.component.searchengine.SearchEngine;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Leo
 * @Blog: http://blog.csdn.net/lc0817
 * @CreateTime: 2016/11/15 11:30
 * @Description: kv搜索引擎, 支持精确key, 模糊value搜索
 * 只允许选择精确搜索或模糊搜索其一,不能同时选择.
 */
public class KvSearchEngine extends AbstractSearchEngine implements SearchEngine<Set<ContextInfo>> {


    private ConcurrentHashMap<String, ConcurrentHashMap<String, Set<KvIndex>>> kvIndexMap;
    private ConcurrentHashMap<Long, ContextInfo> contextInfoMap;


    public KvSearchEngine(ConcurrentHashMap<String, ConcurrentHashMap<String, Set<KvIndex>>> kvIndexMap, ConcurrentHashMap<Long, ContextInfo> contextInfoMap, SearchConditionDTO searchCondition) throws Exception {
        this.kvIndexMap = kvIndexMap;
        this.contextInfoMap = contextInfoMap;
        if (searchCondition.getBeginDateTime().after(searchCondition.getEndDateTime())) {
            throw new Exception("开始时间不能小于结束时间");
        }
        this.searchCondition = searchCondition;
    }

    @Override
    public Set<ContextInfo> search() throws Exception {
        ConcurrentHashMap<String, Set<KvIndex>> valueMap = kvIndexMap.get(searchCondition.getKey());
        Set<KvIndex> kvIndexSet = null;

        if (searchCondition.isFuzzy()) {
            Set<String> valueSet = valueMap.keySet();
            if (null == valueSet || valueSet.isEmpty()) {

            } else {
                kvIndexSet = new HashSet<>(valueSet.size() << 1);//避免扩容,
                for (String value : valueSet) {
                    if (value.contains(searchCondition.getValue())) {
                        kvIndexSet.addAll(valueMap.get(value));
                    }
                }
            }
        } else {
            if (null == valueMap || valueMap.isEmpty()) {

            } else {
                kvIndexSet = valueMap.get(searchCondition.getValue());
            }
        }

        if (null == kvIndexSet || kvIndexSet.isEmpty()) {
            return new HashSet<>();
        } else {
            matchedContextInfoSet = new HashSet<>(kvIndexSet.size());
            for (KvIndex kvIndex : kvIndexSet) {
                Long contextCount = kvIndex.getContextCount();
                ContextInfo contextInfo = contextInfoMap.get(contextCount);
                if (inDateTimeRange(contextInfo)) {
                    matchedContextInfoSet.add(contextInfo);
                }
            }
            return matchedContextInfoSet;
        }
    }
}
