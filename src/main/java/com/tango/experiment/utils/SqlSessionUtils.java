package com.tango.experiment.utils;

import com.tango.experiment.server.mapper.DocMapper;
import com.tango.experiment.server.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class SqlSessionUtils {
    private static SqlSession getSqlSession() {
        try {
            InputStream in = Resources.getResourceAsStream("mybatis-config.xml");
            SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
            SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(in);
            return sqlSessionFactory.openSession(true);
        } catch (IOException e) {
            log.error("SqlSessionUtils getSqlSession error:{}", e.getMessage());
            return null;
        }
    }

    public static UserMapper getUserMapper() {
        SqlSession sqlSession = getSqlSession();
        assert sqlSession != null;
        return sqlSession.getMapper(UserMapper.class);
    }

    public static DocMapper getDocMapper() {
        SqlSession sqlSession = getSqlSession();
        assert sqlSession != null;
        return sqlSession.getMapper(DocMapper.class);
    }
}
