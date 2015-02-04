/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example.cdi.transaction;

import example.cdi.logger.WithLog;
import example.entity.Team;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * サービス
 */
@Transactional // このクラスのメソッドを起点にトランザクションを開始する。
@RequestScoped
@WithLog
public class SampleService {
    
    // DAOをインジェクション
    @Inject
    private TeamDao dao;
    
    /**
     * 新しいチームを登録する
     * 
     * 例外を発生させるため、引数が "check"で始まる場合チェック例外
     * "uncheck"の場合、非チェック例外を発生させる。
     * @param teamName チーム名
     * @throws  SampleException checkxxが引数の場合
     */
    public void newTeam(String teamName) throws SampleException{
        
        Team t = new Team();
        t.setName(teamName);
        dao.register(t);
        
        // daoの実行後に、例外判定を行う。
        if (teamName.startsWith("check")) {
            throw new SampleException("check exception throws.");
        }
        if (teamName.equals("unchecked")) {
            throw new RuntimeException("uncheck exception throws.");
        }
    }
    
    public List<Team> allTeams() {
        return dao.findAll();
    }
}
