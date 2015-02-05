/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example.cid.util;

import example.cdi.logger.WithLog;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Stereotype;
import javax.transaction.Transactional;

/**
 * サービスクラス標準のアノテーションの組み合わせ
 */
@Retention(RUNTIME)
@Target({TYPE})
@Stereotype // ステレオタイプ宣言
// 以下が各クラスに付与するCDI関係のアノテーション
@Transactional
@RequestScoped
@WithLog
public @interface Service {
}
