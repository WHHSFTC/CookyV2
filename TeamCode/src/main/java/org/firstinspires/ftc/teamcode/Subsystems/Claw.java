package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Claw {
    private final Servo clawServo; // final means constant
    private double position1 = 0.0;  // Default closed position
    private double position2 = 1.0;  // Default open position
    private double speed = 1.0;      // Default Speed factor for gradual movement

    // Constructor, when declaring a servo, name must be provided
    public Claw(HardwareMap hardwareMap, String servoName) {
        clawServo = hardwareMap.get(Servo.class, servoName);
    }

    // Method to set speed factor (range: 0.1 to 1.0 for smooth control)
    public void setSpeed(double newSpeed) {
        speed = Math.max(0.1, Math.min(newSpeed, 1.0)); // Limit range
    }

    // Set positions for opening and closing
    public void setPositions(double pos1, double pos2) {
        position1 = pos1;
        position2 = pos2;
    }

    // Open the claw gradually
    public void open() {
        moveServo(position2);
    }

    // Close the claw gradually
    public void close() {
        moveServo(position1);
    }

    // Helper function for smooth movement
    private void moveServo(double targetPosition) {
        double currentPosition = clawServo.getPosition();

        while (Math.abs(currentPosition - targetPosition) > 0.01) { // Small threshold to stop
            currentPosition += Math.signum(targetPosition - currentPosition) * speed * 0.01;
            clawServo.setPosition(currentPosition);

            try {
                Thread.sleep(10); // Short delay for smoother motion
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        clawServo.setPosition(targetPosition); // Ensure final position
    }
}
