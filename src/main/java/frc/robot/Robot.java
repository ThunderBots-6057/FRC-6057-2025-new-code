// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;


import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.Spark;

import java.util.function.BooleanSupplier;
import java.util.function.LongSupplier;




public class Robot extends TimedRobot {
  private DifferentialDrive m_robotDrive;
  private Joystick m_driver;
  private Joystick m_operator;


  private final Spark m_leftFrontMotor = new Spark(4);
  private final Spark m_rightFrontMotor = new Spark(5);
  private final Spark m_leftRearMotor = new Spark(51);
  private final Spark m_rightRearMotor = new Spark(3);
  private final Spark m_algeIntakeArm = new Spark(6);
  private final Spark m_algeIntake = new Spark(7);
  private final Spark m_coralIntake = new Spark(8);
  private final Spark m_coralIntakeAngle = new Spark(9);
  private final Spark m_climb = new Spark(10);




  @Override
  public void robotInit() {
    // we need one side of the divetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead
    m_rightFrontMotor.setInverted(true);
    m_rightRearMotor.setInverted(true);
    
    //make the rears follow the fronts
    m_leftRearMotor.setInverted(m_leftFrontMotor.getInverted());
    m_rightRearMotor.setInverted(m_rightFrontMotor.getInverted());

    //Set up dive and control systems
    m_robotDrive = new DifferentialDrive(m_leftFrontMotor::set, m_rightFrontMotor::set);
    m_driver = new Joystick(0);
    m_operator = new Joystick(1);
  }
      //Set the neutral mode to brake
      @Override
    public void teleopPeriodic() {
         m_robotDrive.tankDrive(-m_driver.getRawAxis(1)*0.6,m_driver.getRawAxis(5)*0.6);
     

  }}
