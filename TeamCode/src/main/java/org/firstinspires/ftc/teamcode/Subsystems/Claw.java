package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Claw {
    private final Servo clawServo;
    /**
     * Value for the default closed position (Tune in the Claw Class file)
     */
    private double position1 = .2; // TUNE Default closed position
    /**
     * Value for the default open position (Tune in the Claw Class file)
     */
    private double position2 = .53; // TUNE Default open position
    /**
     * Value for the default speed factor (Tune in the Claw Class file)
     */

    /**
     * Constructor: Initializes the PID controller with given tuning values.
     *
     * @param hardwareMap write "hardwareMap" in the call to pass the Hardware Map to the Claw class
     * @param servoName   {String} - Name of the claw servo
     */
    public Claw(HardwareMap hardwareMap, String servoName) {
        clawServo = hardwareMap.get(Servo.class, servoName);

    }

    // Set positions for opening and closing
    public void setPosition1(double pos1) {
        position1 = pos1;
    }

    public void setPosition2(double pos2) {
        position2 = pos2;
    }

    public void openClaw() {
        clawServo.setPosition(position1);
    }

    public void closeClaw() {
        clawServo.setPosition(position2);
    }

    public double getServoPosition() {
        return clawServo.getPosition();
    }

    public double getPosition1() {
        return position1;
    }

    public double getPosition2() {
        return position2;
    }

    public boolean isOpen() {
        if (clawServo.getPosition() == position1) return true;
        else return false;
    }
}