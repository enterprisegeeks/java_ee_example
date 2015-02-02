/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example.cdi.transaction;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * EntityManagerのProducer
 */
@Dependent
public class EMProducer {
    @PersistenceContext(name = "default")
    @Produces
    private EntityManager em;
}
