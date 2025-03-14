// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;


//import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.button.POVButton;

import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.LongSupplier;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;

// See https://docs.revrobotics.com/revlib/install#c++-and-java-installation
// For how to install revrobotics library
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkMaxConfig;

// Imports from last year for camera and shuffleboard/dashboard
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.shuffleboard.SuppliedValueWidget;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.CvSink;
import edu.wpi.first.cscore.CvSource;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.cscore.VideoMode;
import edu.wpi.first.cscore.VideoSource.ConnectionStrategy;
import edu.wpi.first.util.PixelFormat;
import edu.wpi.first.util.sendable.SendableRegistry;


public class Robot extends TimedRobot {
  private DifferentialDrive m_robotDrive;
  private XboxController m_driver;
  private XboxController m_operator;

  private final SparkMaxConfig c_leftFrontMotor = new SparkMaxConfig();
  private final SparkMax m_leftFrontMotor = new SparkMax(4, MotorType.kBrushed);
  private final SparkMaxConfig c_rightFrontMotor = new SparkMaxConfig();
  private final SparkMax m_rightFrontMotor = new SparkMax(5, MotorType.kBrushed);
  private final SparkMaxConfig c_leftRearMotor = new SparkMaxConfig();
  private final SparkMax m_leftRearMotor = new SparkMax(51, MotorType.kBrushed);
  private final SparkMaxConfig c_rightRearMotor = new SparkMaxConfig();
  private final SparkMax m_rightRearMotor = new SparkMax(3, MotorType.kBrushed);
  private final SparkMax m_algaeIntakeArm = new SparkMax(7, MotorType.kBrushed);
  private final SparkMax m_algaeIntake = new SparkMax(8, MotorType.kBrushed);
  private final SparkMax m_coralIntake = new SparkMax(13, MotorType.kBrushed);
  private final SparkMax m_coralIntakeAngle = new SparkMax(2, MotorType.kBrushed);
  private final SparkMax m_climb = new SparkMax(15, MotorType.kBrushed);

  private boolean b_driveSpeed = true;


  // From last year for shuffleboard/dashboard and camera
  final Timer autonTimer = new Timer();

  UsbCamera camera1;
  UsbCamera camera2;
 
  private static final String kCustomAutoDisabled = "Auton Disabled";
  private static final String kCustomAutoOne = "Auton One";
  private static final String kCustomAutoTwo = "Auton Two";
  private static final String kCustomAutoThree = "Auton Three";
  private static final String kCustomAutoFour = "Auton Four";
  private static final String kCustomAutoFive = "Auton Five";
  private static final String kDefaultAuto = kCustomAutoOne;
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();




  // pnuematics to be added
  // Operator r stick up n down
  private final DoubleSolenoid doubleSolenoid_one = new DoubleSolenoid(0, PneumaticsModuleType.CTREPCM, 0, 1);
  private final DoubleSolenoid doubleSolenoid_two = new DoubleSolenoid(0, PneumaticsModuleType.CTREPCM, 2, 3);

// Timers for debounce
private final Timer t_driveSpeed = new Timer();

private static final boolean camerasConnected = false;
private static final int autonDelay = 5;



  @Override
  public void robotInit() {


    // we need one side of the divetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead
    c_rightFrontMotor.inverted(true);
    c_rightRearMotor.inverted(true);
    
    c_leftFrontMotor.inverted(true);
    c_leftRearMotor.inverted(true);
    
    //make the rears follow the fronts
    c_leftRearMotor.follow(m_leftFrontMotor);
    c_rightRearMotor.follow(m_rightFrontMotor);

    // Apply the configs
    m_leftFrontMotor.configure(c_leftFrontMotor, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    m_leftRearMotor.configure(c_leftRearMotor, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    m_rightFrontMotor.configure(c_rightFrontMotor, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    m_rightRearMotor.configure(c_rightRearMotor, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    //Set up drive and control systems
    //Add drive speed toggle
    m_robotDrive = new DifferentialDrive(m_leftFrontMotor::set, m_rightFrontMotor::set);
    m_driver = new XboxController(0);
    m_operator = new XboxController(1);

    m_autoSelected = m_chooser.getSelected();
    System.out.println("Auto selected: " + m_autoSelected);
    SmartDashboard.putData("Auton", m_chooser);


    // Timers for debounce reset
    t_driveSpeed.reset();
    t_driveSpeed.start();

    // Shuffleboard/dashboard settings
    SendableRegistry.addChild(m_robotDrive, m_leftFrontMotor);
    SendableRegistry.addChild(m_robotDrive, m_rightFrontMotor);

    m_chooser.setDefaultOption(kDefaultAuto, kDefaultAuto);
    m_chooser.addOption(kCustomAutoDisabled, kCustomAutoDisabled);
    m_chooser.addOption(kCustomAutoOne, kCustomAutoOne);
    m_chooser.addOption(kCustomAutoTwo, kCustomAutoTwo);
    m_chooser.addOption(kCustomAutoThree, kCustomAutoThree);
    m_chooser.addOption(kCustomAutoFour, kCustomAutoFour);
    m_chooser.addOption(kCustomAutoFive, kCustomAutoFive);


    m_autoSelected = m_chooser.getSelected();
    System.out.println("Auto selected: " + m_autoSelected);
    SmartDashboard.putData("Auton", m_chooser);

    SmartDashboard.putBoolean("Full Speed", true);

// Camera detect
// try to connect to camera 1
    try {
      //  Block of code to try
      if(camerasConnected) {
        camera1 = CameraServer.startAutomaticCapture(0);
        camera1.setVideoMode(PixelFormat.kMJPEG,640,480,30);
      }

    }

    catch(Exception e) {
      //  Block of code to handle errors
      System.out.println("Camera-1: Not connected " + e.getMessage());
    }

    // try to connect to camera 2
    try {
      if (camerasConnected){
      //  Block of code to try
        camera2 = CameraServer.startAutomaticCapture(1);
        camera2.setVideoMode(PixelFormat.kMJPEG,640,480,30);
      }
    }

    

    catch(Exception e) {
      //  Block of code to handle errors
      System.out.println("Camera-2: Not connected " + e.getMessage());
    }


  }


  // Begin auton code from last year -------------------------------------------------------------
  @Override
  public void autonomousInit(){
    // Reset autonTimer everytime autonomous mode is entered
    autonTimer.reset();
    autonTimer.start();

    // read selected auton and show what was chosen
    m_autoSelected = m_chooser.getSelected();
    System.out.println("Auto selected: " + m_autoSelected);

    
  }

  @Override
  public void autonomousPeriodic(){
    
    // Perform selected Auton
    switch(m_autoSelected) {
      case kCustomAutoDisabled:
        // Do nothing
        break;
      case kCustomAutoOne:
//        if(!IntakeUp.get()) {
//        m_intake_lift.set(-0.25); //up
//        }

        // Drive forward 50% for 5 seconds
        if (5 >= autonTimer.get()) {
          m_robotDrive.tankDrive(-0.714, 0.714);
        } else {
          m_robotDrive.tankDrive(0, 0);
        }

        // Drive from 5 to 10 drop coral at 10%
        if ((5 <= autonTimer.get()) && (10 >= autonTimer.get())) {
          m_coralIntake.set(-0.1);
        } else {
          m_coralIntake.set(0);
        }

        break;
      case kCustomAutoTwo:
        // code block for Auton Two
        ///////////////////////////////////  Start
        // Drive forward 50% for 5 seconds
        if (2 >= autonTimer.get()) {
          m_robotDrive.tankDrive(0.714, -0.714);
        } else {
          m_robotDrive.tankDrive(0, 0);
        }


        ///////////////////////////////////  End
        break;
      case kCustomAutoThree:
        // code block for Auton Three
        if (2 >= autonTimer.get()) {
          m_robotDrive.tankDrive(0.360, -0.360);
        } else {
          m_robotDrive.tankDrive(0, 0);
        }
        break;
      case kCustomAutoFour:
        // code block for Auton Four
        break;
      case kCustomAutoFive:
        // code block for Auton Five
        break;
      default:
        System.out.println("Warning: No Auton selected");
    }

  }

  // End auton code from last year ---------------------------------------------------------------


      //Set the neutral mode to brake
      @Override
    public void teleopPeriodic() {

      if (m_driver.getRawButton(6) && (t_driveSpeed.get() >= 1)){
        b_driveSpeed = !b_driveSpeed;
        SmartDashboard.putBoolean("Full Speed", b_driveSpeed);
         t_driveSpeed.reset();
     }

     if ((m_driver.getRawAxis(1) > 0.05) || (m_driver.getRawAxis(1) < -0.05) || (m_driver.getRawAxis(5) > 0.05) || (m_driver.getRawAxis(5) < -0.05)){ 
     if(b_driveSpeed){
       m_robotDrive.tankDrive(-m_driver.getRawAxis(1),m_driver.getRawAxis(5));
     }else {
       m_robotDrive.tankDrive(-m_driver.getRawAxis(1)*0.8,m_driver.getRawAxis(5)*0.8);
     }
     } else{
      m_robotDrive.tankDrive(0, 0);
     }
    

      

      //Algae Intake Arm
      if ((m_operator.getPOV(0) == 225) || (m_operator.getPOV(0) == 180) || (m_operator.getPOV(0) == 135)){
       m_algaeIntakeArm.set(1);
      } else if ((m_operator.getPOV(0) == 315) || (m_operator.getPOV(0) == 0) || (m_operator.getPOV(0) == 45)) {
       m_algaeIntakeArm.set(-1);
      } else {
       m_algaeIntakeArm.set(0);
      }

      //Algae Intake
      if(m_operator.getRawAxis(3) >= 0.5 ){
//        m_algaeIntakeArm.set(1);
        m_algaeIntake.set(1);
      } else if (m_operator.getRawAxis(2) >= 0.5){
       m_algaeIntake.set(-1);
//       m_algaeIntakeArm.set(-1);
      } else {
       m_algaeIntake.set(0);
//       m_algaeIntakeArm.set(0);
      }

      //Coral Intake
      if(m_operator.getRawButton(6)){
        m_coralIntake.set(1);
      } else if (m_operator.getRawButton(5)){
        m_coralIntake.set(-1);
      } else {
       m_coralIntake.set(0);
      }
      
      //Coral Arm
      if (m_operator.getRawAxis(1) > 0.1){
       m_coralIntakeAngle.set(-1);
      } else if(m_operator.getRawAxis(1) < -0.1){
       m_coralIntakeAngle.set(1);
      } else {
       m_coralIntakeAngle.set(0);
      }

      //Coral Arm Elevator (pnuematics)
        if (m_operator.getRawAxis(5) <= -0.5) {
          doubleSolenoid_one.set(DoubleSolenoid.Value.kForward);
          doubleSolenoid_two.set(DoubleSolenoid.Value.kForward);
          SmartDashboard.putString("Solenoid State", "Forward");
        } else if (m_operator.getRawAxis(5) >= 0.5) {
          doubleSolenoid_one.set(DoubleSolenoid.Value.kReverse);
          doubleSolenoid_two.set(DoubleSolenoid.Value.kReverse);
          SmartDashboard.putString("Solenoid State", "Reverse");
        } else {
          doubleSolenoid_one.set(DoubleSolenoid.Value.kOff);
          doubleSolenoid_two.set(DoubleSolenoid.Value.kOff);
          SmartDashboard.putString("Solenoid State", "Off");
        }

      //Climber Elevator
      if (m_driver.getRawAxis(3) >= 0.5){
       m_climb.set(1);
      } else if (m_driver.getRawAxis(2) >= 0.5){
       m_climb.set(-1);
      } else {
       m_climb.set(0);
      }

      
    }}
