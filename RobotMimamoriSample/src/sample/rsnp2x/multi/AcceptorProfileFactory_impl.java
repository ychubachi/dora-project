/*
 * $Id: AcceptorProfileFactory_impl.java 311 2010-05-14 02:25:39Z mitsuki $
 *
 * Copyright 2009-2010 Fujitsu Limited.
 * FUJITSU CONFIDENTIAL.
 */
package sample.rsnp2x.multi;

import org.robotservices.v02.profile.acceptor.IMultimedia_profile;

import com.fujitsu.rsi.client.acceptor.base.AcceptorProfileFactoryBase;

/**
 * AcceptorProfileFactoryの実装クラス<br>
 * AcceptorProfileFactoryBaseを継承しているため、不要なメソッドを実装する必要がない
 */

public class AcceptorProfileFactory_impl extends AcceptorProfileFactoryBase {

	/*
	 * (非 Javadoc)
	 *
	 * @seecom.fujitsu.rsi.client.acceptor.base.AcceptorProfileFactoryBase#
	 * getMultimedia_profile()
	 */
	@Override
	public IMultimedia_profile getMultimedia_profile() {
		return new Multimedia_profile_impl();
	}

}
