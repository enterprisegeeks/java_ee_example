/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example.cdi.transaction;

import example.entity.Team;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import org.jglue.cdiunit.CdiRunner;
import org.jglue.cdiunit.InRequestScope;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.runner.RunWith;

/**
 * CDIUnitによるDIを行うテスト
 * 
 * 宣言的トランザクションはサポートされないので、トランザクション制御は自前で行う。
 */
@RunWith(CdiRunner.class) // CDIUnitの動作で必須。
public class SampleServiceTestByCDI {
    
    @Inject // テスト対象
    SampleService target;
    
    @Inject // トランザクション制御で使用。targetの内部で使用するEntityMangerと共有。
    EntityManager em;
    
    EntityManagerFactory emf;
    
    // 初期処理
    @PostConstruct
    void init() {
      emf = Persistence.createEntityManagerFactory("ut");
    }
    
    /** UT用のEntityManagerの生成 */
    @Produces @ApplicationScoped
    EntityManager createUtEm() {
        System.out.println("called");
        return emf.createEntityManager();
    }
    
    /** トランザクションの開始 */
    @Before
    // このアノテーションにより、各テストクラスのメソッドや、対象オブジェクト内のインジェクション対象が共有される。
    @InRequestScope
    public void setUp() {
        em.getTransaction().begin();
        em.persist(TeamOf("A", 1));
        em.flush();
        
    }
    
    /** ロールバック */
    @After
    @InRequestScope
    public void tearDown() {
        em.getTransaction().rollback();
    }

    /** 1件登録を行うケース */
    @Test
    @InRequestScope
    public void testNewTeamOnNormal() throws Exception {
        
        target.newTeam("B");
        
        assertThat(em.find(Team.class, "B"), is(TeamOf("B", 1)));
    }
    
    /** チェック例外が起きるケース */
    @Test(expected = SampleException.class)
    @InRequestScope
    public void testCheckException() throws Exception {
        try {
            target.newTeam("checkng");
        } catch(SampleException e) {
            em.flush();
            assertThat(em.find(Team.class, "checkng"), is(TeamOf("checkng", 1)));
            throw e;
        }
    }
    
    /** 非チェック例外の発生。(トランザクションのロールバックは検証対象外) */
    @Test(expected = RuntimeException.class)
    @InRequestScope
    public void testUnCheckException() throws Exception {
        target.newTeam("unchecked");
    }
    
    /** 同一のエンティティをインサートするケース */
    @Test(expected = PersistenceException.class)
    @InRequestScope
    public void testEntityExists() throws Exception {
        
            target.newTeam("A");
    }
    
    private static Team TeamOf(String name, long version) {
        Team t = new Team();
        t.setName(name);
        t.setVersion(version);
        return t;
    }
    
}
