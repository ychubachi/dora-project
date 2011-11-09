package rsnp.acceptor;

import org.robotservices.v02.profile.common.Ret_value;
import blueTooth.BTSend;
import com.fujitsu.rsi.client.acceptor.base.MotionProfileBase;

public class Motion_profile_impl extends MotionProfileBase {
	/**
	 * NXTとのBluetooth接続を行う
	 */
	static BTSend bs = new BTSend();

	@Override
	public Ret_value declare_control(long conv_id) {
		Ret_value ret = new Ret_value();
		System.out.println("in the motionProfile declare");
		return ret;
	}

	@Override
	public Ret_value release_control(long conv_id) {
		Ret_value ret = new Ret_value();
		System.out.println("in the motionProfile release");
		bs.closeBT();
		return ret;
	}

	@Override
	public Ret_value forward(long conv_id, double distance, String option) {
		Ret_value ret = new Ret_value();
		System.out.println("in the motionProfile forward");
		bs.selectFunc(8, distance);
		return ret;
	}

	@Override
	public Ret_value backward(long conv_id, double distance, String option) {
		Ret_value ret = new Ret_value();
		System.out.println("in the motionProfile back");
		bs.selectFunc(2, distance);
		return ret;
	}

	@Override
	public Ret_value right(long conv_id, double radius, double degree,
			String option) {
		Ret_value ret = new Ret_value();
		System.out.println("in the motionProfile right");
		bs.selectFunc(6, radius);
		return ret;
	}

	@Override
	public Ret_value left(long conv_id, double radius, double degree,
			String option) {
		Ret_value ret = new Ret_value();
		System.out.println("in the motionProfile left");
		bs.selectFunc(4, radius);
		return ret;
	}

	@Override
	public Ret_value spin_right(long conv_id, double degree, String option) {
		Ret_value ret = new Ret_value();
		System.out.println("in the motionProfile spinRight");
		bs.selectFunc(6, degree);
		return ret;
	}

	@Override
	public Ret_value spin_left(long conv_id, double degree, String option) {
		Ret_value ret = new Ret_value();
		System.out.println("in the motionProfile spinLeft");
		bs.selectFunc(4, degree);
		return ret;
	}

	@Override
	public Ret_value stop(long conv_id, String option) {
		Ret_value ret = new Ret_value();
		System.out.println("in the motionProfile stop");
		bs.selectFunc(5, 0);
		return ret;
	}
}
