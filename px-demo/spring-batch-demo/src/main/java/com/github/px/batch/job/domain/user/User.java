package com.github.px.batch.job.domain.user;

import lombok.Getter;
import lombok.Setter;
import com.github.px.domain.BaseEntity;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "tb_user")
public class User extends BaseEntity{
    private String name;

    private String mobile;
}
