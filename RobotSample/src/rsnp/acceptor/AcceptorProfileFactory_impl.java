/*
 * $Id: AcceptorProfileFactory_impl.java 272 2010-04-28 07:11:27Z itoh $
 *
 * Copyright 2009-2010 Fujitsu Limited.
 * FUJITSU CONFIDENTIAL.
 */
package rsnp.acceptor;

import org.robotservices.v02.profile.acceptor.IMultimedia_profile;
import org.robotservices.v02.profile.acceptor.IMotion_profile;

import com.fujitsu.rsi.client.acceptor.base.AcceptorProfileFactoryBase;

/**
 * AcceptorFactoryの実装クラス<br>
 * AcceptorProfileFactoryBaseを継承しているため、不要なメソッドを実装する必要がない
 */
public class AcceptorProfileFactory_impl extends AcceptorProfileFactoryBase {

	@Override
	public IMultimedia_profile getMultimedia_profile() {
		return new Multimedia_profile_impl();
	}
	
	@Override
	public IMotion_profile getMotion_profile() {
		return new Motion_profile_impl();
	}
}
