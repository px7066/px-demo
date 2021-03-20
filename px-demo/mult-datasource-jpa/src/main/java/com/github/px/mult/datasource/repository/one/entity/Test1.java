package com.github.px.mult.datasource.repository.one.entity;


import com.github.px.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "tb_test1")
@Setter
@Getter
public class Test1 extends BaseEntity {


}
