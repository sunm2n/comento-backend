package com.demo.devfun.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class HomeDaoImpl implements HomeDao {

    @Autowired
    private SqlSession sqlSession;

    @Override
    public int getTestCount() {
        return sqlSession.selectOne("com.test.springTest.testxml.selectTest");
    }

    @Override
    public List<Map<String, Object>> getRequests() {
        return sqlSession.selectList("com.test.springTest.testxml.selectTest2");
    }
}