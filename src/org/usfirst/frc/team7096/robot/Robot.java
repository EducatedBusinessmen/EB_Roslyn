/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team7096.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import java.lang.Math.*;

public class Robot extends IterativeRobot {
	
	Joystick operator = new Joystick(0);
	Joystick joystick_L = new Joystick(1);
	Joystick joystick_R = new Joystick(2);
	Joystick gamepad = new Joystick(3);
	
	WPI_TalonSRX T1 = new WPI_TalonSRX(1);
	WPI_TalonSRX T2 = new WPI_TalonSRX(2);
	WPI_TalonSRX T3 = new WPI_TalonSRX(3);
	WPI_TalonSRX T4 = new WPI_TalonSRX(4);
	
	SpeedControllerGroup m_left = new SpeedControllerGroup(T1, T4);
	SpeedControllerGroup m_right = new SpeedControllerGroup(T2, T3);
	DifferentialDrive robotDrive = new DifferentialDrive(m_left, m_right);

	
	Timer timer = new Timer();
	
	double vMult = 0;
	double a = 0;
	
	
	boolean previousButton = false;
	boolean currentButton = false;
	boolean vHalf = false;
	
	@Override
	public void robotInit() {
		CameraServer.getInstance().startAutomaticCapture(0);
		CameraServer.getInstance().startAutomaticCapture(1);
	}
	
	@Override
	public void autonomousInit() {
		timer.reset();
		timer.start();
	}
	
	@Override
	public void autonomousPeriodic() {
		
		if (timer.get() < 1.0) {
			robotDrive.arcadeDrive(0.4, 0);
		}
		else if(timer.get() < 3.0) {
			robotDrive.arcadeDrive(0, 0.4);
		}
		else if(timer.get() < 5.0) {
			robotDrive.arcadeDrive(0, -0.4);
		}
		else {
			robotDrive.stopMotor();
		}
	}
		
	@Override
	public void teleopInit() {
		vMult = 0;
		robotDrive.stopMotor();
		
		previousButton = false;
		currentButton = false;
		vHalf = false;
		
		a = 0.8; //constant (0 to 1)
	}

	boolean tankDriveMode = true;
	
	public double joyMod(double x) {
		double xP = 0;
		if(Math.abs(x) < 0.01) {
			x = (int)x;
			x = (double)x;
		}
		if(x<0) {
			x = Math.abs(x);
			xP = a*(Math.pow(x, (1.0/3.0))) + (1-a)*x;
			xP = -xP;
		}
		else {
			xP = a*(Math.pow(x, (1.0/3.0))) + (1-a)*x;
		}
		return xP;
	}
	
	@Override
	public void teleopPeriodic() {
		
		a = (joystick_L.getThrottle() + 1)/2;
		vMult = (joystick_R.getThrottle() + 1)/2;
		System.out.println("sens " + a);
		System.out.println("volts " + vMult);
		
		previousButton = currentButton;
		currentButton = operator.getRawButton(10);

		if (currentButton && !previousButton) 
		{
			vHalf = !vHalf;
			
			if(vHalf) {
				System.out.println("toggle_1");
			}
			else {
				System.out.println("toggle_2");
			}
		}
		
		//Drive Mode Set
		if (joystick_L.getRawButton(2) || gamepad.getRawButton(2)) {
			tankDriveMode = !tankDriveMode;
			System.out.println("Tank Drive" + tankDriveMode);
		}
		//Arcade Drive
		if (!tankDriveMode) {
			robotDrive.arcadeDrive(joystick_L.getY(), joystick_L.getX());
		}
		//Tank Drive
		else {
			robotDrive.tankDrive(gamepad.getRawAxis(1), gamepad.getRawAxis(5));
			
			double R = joyMod(joystick_R.getRawAxis(1));
			double L = joyMod(joystick_L.getRawAxis(1));
			//System.out.println(R + " " + L);
			robotDrive.tankDrive(L*vMult, R*vMult);
		}
		
		//prints
		if (!tankDriveMode && joystick_L.getRawButton(5)) {
			System.out.println(joystick_L.getPOV());
		}
		else if (tankDriveMode && gamepad.getRawButton(5)) {
			System.out.println(gamepad.getPOV());
		}
		else if (!tankDriveMode && joystick_L.getRawButton(3)) {
			System.out.println(joystick_L.getPort());
		}
		
		else if (tankDriveMode && gamepad.getRawButton(3)) {
			System.out.println(gamepad.getPort());
		}
	}
	
	@Override
	public void testPeriodic() {
		
	}
}
