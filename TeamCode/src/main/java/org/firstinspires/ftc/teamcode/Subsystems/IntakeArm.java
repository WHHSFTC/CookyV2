package org.firstinspires.ftc.teamcode.Subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class IntakeArm {
    private final Servo leftServo, rightServo, wristServo;
    private double intakePosition = 0.0; // TUNE HERE
    private double hoverPosition = 0.5; // TUNE HERE
    private double transferPosition = 1.0; // TUNE HERE
    private double wristPosition = 0.5; // TUNE HERE - set this to be the center position for the wrist

    private double wristLeftPosition = .18, wristRightPosition = .82; // TUNE HERE - currently tuned via FTC dashboard, public static is visible in dashboard

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
    }

    public void moveToIntake() {
        moveArmServos(intakePosition);
    }

    public void moveToHover() {
        moveArmServos(hoverPosition);
    }

    public void moveToTransfer() {
        moveArmServos(transferPosition);
    }

    /**
     * Allows for moving the wrist to an exact specified position
     *
     * @param targetPosition exact position to turn servo to
     */

    public void moveWrist(double targetPosition) {
        wristServo.setPosition(targetPosition);
    }

    public void wristLeftPos() {
        wristServo.setPosition(wristLeftPosition);
    }

    public void wristRightPos() {
        wristServo.setPosition(wristRightPosition);
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

    private void moveArmServos(double targetPosition) { //Automatically reversed arm servos based on target pos
        leftServo.setPosition(targetPosition);
        rightServo.setPosition(1.0 - targetPosition);
    }
}