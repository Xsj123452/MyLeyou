package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SpecificationService {
    @Autowired
    private SpecGroupMapper specGroupMapper;
    @Autowired
    private SpecParamMapper specParamMapper;

    /**
     * 通过分类id查询规格组
     * @param cid
     * @return
     */
    public List<SpecGroup> queryGroupById(Long cid){
        SpecGroup group = new SpecGroup();
        group.setCid(cid);
        List<SpecGroup> list = specGroupMapper.select(group);
        if(CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.GROUP_SPEC_NOT_FOUND);
        }
        return list;
    }

    /**
     * 通过分类id查询规格参数
     * @param cid
     * @return
     */
//    public List<SpecParam> queryParamByCid(Long cid){
//        List<SpecParam> params = specGroupMapper.querySpecParamByCid(cid);
//        if(CollectionUtils.isEmpty(params)){
//            throw new LyException(ExceptionEnum.THE_CATEGORY_NOT_HAVE_PARAM);
//        }
//        return params;
//    }

    /**
     * 新增规格组
     * @param specGroup
     */
    @Transactional
    public void saveGroup(SpecGroup specGroup){
        specGroupMapper.insert(specGroup);
    }

    /**
     * 删除Group
     * @param id
     */
    @Transactional
    public void deleteGroupById(Long id) {
        specGroupMapper.deleteGroupParamByGid(id);
        specGroupMapper.deleteByPrimaryKey(id);
    }

    /**
     * 通过分类id查询规格参数
     * @param gid
     * @param cid
     * @param searching
     * @return
     */
    public List<SpecParam> querySpecParamByGid(Long gid,Long cid,Boolean searching){
        SpecParam sp = new SpecParam();
        sp.setGroupId(gid);
        sp.setCid(cid);
        sp.setSearching(searching);
        //通过gid，cid或者是否查询来查询规格参数
        List<SpecParam> specParams = specParamMapper.select(sp);
        if(CollectionUtils.isEmpty(specParams)){
            throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }
        return specParams;
    }

    public List<SpecGroup> queryListByCid(Long cid) {
        List<SpecGroup> groups = specGroupMapper.queryGroupByCid(cid);
        //查询当前分类下参数
        List<SpecParam> specParams = querySpecParamByGid(null, cid, null);
        //吧规格参数转换成map，map的key为规格组id，值为组下所有参数
        Map<Long,List<SpecParam>> map = new HashMap<>();
        for (SpecParam param : specParams) {
            if (!map.containsKey(param.getGroupId())) {
                map.put(param.getGroupId(), new ArrayList<>());
            }
            map.get(param.getGroupId()).add(param);

        }
        for (SpecGroup specGroup : groups) {
            specGroup.setParams(map.get(specGroup.getId()));
        }
//        for (SpecParam specParam : specParams) {
//            if(!map.containsKey(specParam.getGroupId())){
//                //如果组不存在则新增一个list到值里
//                map.put(specParam.getGroupId(),new ArrayList<>());
//            }
//            map.get(specParam.getGroupId()).add(specParam);
//        }
//        //将param放进group
//        for (SpecGroup group : groups) {
//            group.setParams(map.get(group.getCid()));
//        }

//        for (SpecGroup group : groups) {
//            for (SpecParam specParam : specParams) {
//                if(group.getId().equals(specParam.getGroupId())){
//                    group.setParams(specParams);
//                }
//            }
//        }
        return groups;
    }
}
