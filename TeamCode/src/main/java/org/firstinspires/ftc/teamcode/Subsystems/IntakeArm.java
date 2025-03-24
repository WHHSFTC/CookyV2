package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.hardware.lynx.LynxModule;

import java.util.List;

public class IntakeArm {
    private final Servo leftServo, rightServo, wristServo;
    private double intakePosition = 0.0; // TUNE HERE
    private double transferPosition = 1.0; // TUNE HERE
    private double wristPosition = 0.5; // TUNE HERE - set this to be the center position for the wrist
    private double speed = 1.0;
    private final List<LynxModule> allHubs;

    /**
     * Constructor for the Intake Arm.
     *
     * @param hardwareMap    Hardware map to get servo reference.
     * @param leftServoName  Name of the left servo in the configuration.
     * @param rightServoName Name of the right servo in the configuration.
     * @param wristServoName Name of the wrist servo in the configuration.
     */
    public IntakeArm(HardwareMap hardwareMap, String leftServoName, String rightServoName, String wristServoName) {
        leftServo = hardwareMap.get(Servo.class, leftServoName);
        rightServo = hardwareMap.get(Servo.class, rightServoName);
        wristServo = hardwareMap.get(Servo.class, wristServoName);

        allHubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }
    }

    public void updateBulkRead() {
        for (LynxModule hub : allHubs) {
            hub.clearBulkCache();
        }
    }

    public void moveToIntake() {
        moveServo(intakePosition);
    }

    public void moveToTransfer() {
        moveServo(transferPosition);
    }

    public void moveWrist(double targetPosition) {
        wristServo.setPosition(targetPosition);
    }

    public void setSpeed(double newSpeed) {
        speed = Math.max(0.1, Math.min(newSpeed, 1.0));
    }

    public void setIntakePosition(double pos) {
        intakePosition = pos;
    }

    public void setTransferPosition(double pos) {
        transferPosition = pos;
    }

    public double getLeftServoPosition() {
        return leftServo.getPosition();
    }

    public double getRightServoPosition() {
        return rightServo.getPosition();
    }

    public double getWristServoPosition() {
        return wristServo.getPosition();
    }

    public double getIntakePosition() {
        return intakePosition;
    }

    public double getTransferPosition() {
        return transferPosition;
    }

    public double getSpeed() {
        return speed;
    }

    private void moveServo(double targetPosition) {
        double leftPosition = leftServo.getPosition();
        double rightPosition = 1.0 - leftPosition;

        while (Math.abs(leftPosition - targetPosition) > 0.01) {
            leftPosition += Math.signum(targetPosition - leftPosition) * speed * 0.01;
            rightPosition = 1.0 - leftPosition;

            leftServo.setPosition(leftPosition);
            rightServo.setPosition(rightPosition);

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        leftServo.setPosition(targetPosition);
        rightServo.setPosition(1.0 - targetPosition);
    }
}