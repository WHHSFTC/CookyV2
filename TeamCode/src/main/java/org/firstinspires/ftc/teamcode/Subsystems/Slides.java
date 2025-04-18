package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.hardware.lynx.LynxModule;

import java.util.List;

public class Slides {
    private final DcMotorEx motor;
    private final PIDController pid;
    private final List<LynxModule> hubs;

    private int targetPosition = 0;

    /**
     * PID Constructor - You provide PID values
     */
    public Slides (HardwareMap hardwareMap, String motorName, double kP, double kI, double kD) {
        motor = hardwareMap.get(DcMotorEx.class, motorName);
        motor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        motor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        pid = new PIDController(kP, kI, kD);
        hubs = hardwareMap.getAll(LynxModule.class);
    }
    /**
     * No-PID Constructor - PID values are set to defaults: <br>
     * Kp = .01 <br>
     * kI = 0.0 <br>
     * kD = 0.0001
     */
    public Slides (HardwareMap hardwareMap, String motorName) {
        motor = hardwareMap.get(DcMotorEx.class, motorName);
        motor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        motor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        pid = new PIDController(0.01, 0.0, 0.0001);
        hubs = hardwareMap.getAll(LynxModule.class);
    }

    public void goTo(int ticks) {
        targetPosition = ticks;
    }

    public void update() {
        clearBulkCache(); // Only clear if needed
        int currentPosition = motor.getCurrentPosition();
        double power = pid.calculate(targetPosition, currentPosition);
        motor.setPower(power);
    }

    public int getCurrentPosition() {
        clearBulkCache();
        return motor.getCurrentPosition();
    }

    public int getTargetPosition() {
        return targetPosition;
    }

    public void stop() {
        motor.setPower(0);
    }

    public void resetEncoder() {
        motor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        pid.reset();
    }

    private void clearBulkCache() {
        for (LynxModule hub : hubs) {
            hub.clearBulkCache();
        }
    }
}
