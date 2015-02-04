/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example.cdi.transaction;

import example.entity.Team;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 *
 * @author kentaro.maeda
 */
public class SampleServiceTestByMock {
    
    @Mock // モックとして宣言し既存の振る舞いを変更する。
    TeamDao dao;
    
    @InjectMocks // モックを設定するオブジェクトの宣言
    SampleService target = new SampleService();
    
    @Before
    public void setUp() {
        // @InjectMocksが付いているオブジェクトのフィールドに@Mockが付与されたモックを設定。
        MockitoAnnotations.initMocks(this);
        // モック daoのfindAllの振る舞いを変更。
        when(dao.findAll()).thenReturn(Arrays.asList(TeamOf("A", 1), TeamOf("B", 2)));
    }
    // 便利メソッド
    private static Team TeamOf(String name, long version) {
        Team t = new Team();
        t.setName(name);
        t.setVersion(version);
        return t;
    }
    
    /**
     * DAOにモックを使用したテスト
     */
    @Test
    public void testAllTeams() {
        List<Team> result = target.allTeams();
        assertThat(result.size(),  is(2));
        assertThat(result, is(Arrays.asList(TeamOf("A", 1), TeamOf("B", 2))));
    }

}
