/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exmaple.cdi.transaction;

import example.entity.Team;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

/**
 * DAO サンプル
 */
@Transactional // デフォルト(Require)のため、同一トランザクションで実行される。
@Dependent
public class TeamDao {
    
    @Inject
    private EntityManager em;
    
    /** 登録 */
    public void register(Team team) {
        em.persist(team);
        em.flush();
    }
    
    public List<Team> findAll() {
        return em.createQuery("select t from Team t order by t.name", Team.class)
                .getResultList();
    }
}
