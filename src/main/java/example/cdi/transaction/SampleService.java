/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example.cdi.transaction;

import example.cid.util.Service;
import example.entity.Team;
import java.util.List;
import javax.inject.Inject;

/**
 * サービス
 */
@Service
public class SampleService {
    
    // DAOをインジェクション
    @Inject
    private TeamDao dao;
    
    /**
     * 新しいチームを登録する
     * 
     * 例外を発生させるため、引数が 2文字以下の場合チェック例外
     * 空文字の場合、非チェック例外を発生させる。
     * @param teamName チーム名
     * @throws  SampleException checkxxが引数の場合
     */
    public void newTeam(String teamName) throws SampleException{
        
        Team t = new Team();
        t.setName(teamName);
        dao.register(t);
        
        // daoの実行後に、例外判定を行う。
        if (teamName.equals("")) {
            throw new RuntimeException("uncheck exception throws.");
        }
        if (teamName.length() <= 2) {
            throw new SampleException("check exception throws.");
        }
    }
    
    public List<Team> allTeams() {
        return dao.findAll();
    }
}
