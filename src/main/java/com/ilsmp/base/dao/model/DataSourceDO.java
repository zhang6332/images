package com.ilsmp.base.dao.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 数据源
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class DataSourceDO {
    private Long id;
    /**
     * 数据库类型 mysql:MySQL,pgsql:PostgreSQL,oracle:Oracle,sqlserver:SQL Server
     */
    private String databaseType;

    /**
     * 数据源名称
     */
    private String name;

    /**
     * 主机
     */
    private String host;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    private String initDb;
    private String sid;

}