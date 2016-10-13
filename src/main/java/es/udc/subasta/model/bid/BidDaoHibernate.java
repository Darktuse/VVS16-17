package es.udc.subasta.model.bid;

import org.springframework.stereotype.Repository;

import es.udc.pojo.modelutil.dao.GenericDaoHibernate;

@Repository("BidDao")
public class BidDaoHibernate extends GenericDaoHibernate<Bid, Long> implements BidDao{

}
