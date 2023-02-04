// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.can.*;


import com.ctre.phoenix.motorcontrol.FeedbackDevice;
// import java.util.Scanner; //
/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
  private WPI_TalonSRX m_Left;  
  private WPI_TalonSRX m_FLeft;
  private WPI_TalonSRX m_Right;
  private WPI_TalonSRX m_FRight;
  int x = 0;
  int y = 100; // diserd distance 
  double h;
  double setDistance; //inches
  double e = 0; // e represents the error / curent displacement 
  double ei = 0; // ei represents the error intregal / total displacement 
  double kc = 0.5;
  double kp = kc*0.00001;
  double ki = kc*0.00001;
  double ie; // inital offset
  double Fspeed; // represents the final speed of the robot when scaling in speed 
     /* 
  double e = 0; // e represents the error / curent displacement 
  double ei = 0; // ei represents the error intregal / total displacement 
  double kc = 2;
  double kp = kc*0.01;
  double ki = kc*0.0001;
*/


  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
      // TalonSRX is configured with CAN bus address 3

   m_Left = new WPI_TalonSRX(3);
   m_Right = new WPI_TalonSRX(4);
   m_FLeft = new WPI_TalonSRX(1);
   m_FRight = new WPI_TalonSRX(2);

   m_Right.setInverted(true);
   m_FRight.setInverted(true);

   // Clear any non default configuration/settings

   m_Left.configFactoryDefault();
   m_Right.configFactoryDefault();
   m_FLeft.configFactoryDefault();
   m_FRight.configFactoryDefault();
   // A quadrature encoder is connected to the TalonSRX

   m_Left.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
   m_Right.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
   m_FLeft.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
   m_FRight.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);

   // Reset encoder count to 0

   m_Left.setSelectedSensorPosition(0);
   m_Right.setSelectedSensorPosition(0);
   m_FLeft.setSelectedSensorPosition(0);
   m_FRight.setSelectedSensorPosition(0);
  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    SmartDashboard.putNumber("Encoder value 3", m_Left.getSelectedSensorPosition());
    SmartDashboard.putNumber("Encoder value 4", m_Right.getSelectedSensorPosition());
    SmartDashboard.putNumber("Encoder value 1", m_FLeft.getSelectedSensorPosition());
    SmartDashboard.putNumber("Encoder vale 2", m_FRight.getSelectedSensorPosition());
    double inch_L = distance(m_Left.getSelectedSensorPosition());
    double inch_R = distance(m_Right.getSelectedSensorPosition());
    double inch_FL =distance(m_FLeft.getSelectedSensorPosition());
    double inch_FR = distance(m_FRight.getSelectedSensorPosition());
    SmartDashboard.putNumber("Inches 3", inch_L);
    SmartDashboard.putNumber("Inches 4", inch_R);
    SmartDashboard.putNumber("Inches 1", inch_FL);
    SmartDashboard.putNumber("Inches 2", inch_FR);
    SmartDashboard.putNumber("Error", e);
  }
  public double distance(double e){
    double e2 = (e/1440)*8*Math.PI; // One full rotation is ~24 inches
    return (e2);
    // this methed changes the encoder value to inches
  }
  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
   /* System.out.println("Auto selected: " + m_autoSelected);
    System.out.println("How far do you want to travel inches");
    setMotors(0);*/
    /*Scanner input = new Scanner(System.in);
    setDistance = input.nextDouble();
    input.close();

  */
    /*
   if (m_Left.getSelectedSensorPosition()<Five){
   int x = 1;
   }
   else {
   int x = 0;
   }
   */
  }
  // this method sets the motors power value 
  public void setMotors(double x){
    m_Right.set(x);
    m_Left.set(x);
    m_FRight.set(x);
    m_FLeft.set(x);
    // This method sets the motors value 
  }

  public void pidSpeed(WPI_TalonSRX motor, double sea){
    setDistance = sea;
    e = error(sea);
    ei += e;
    motor.set(kp*e + ki*ei);
    m_FLeft.set(kp*e + ki*ei);
    Fspeed = (kp*e + ki*ei);   // This method increases motor speed the longer it takes to get error = 0
  }

  public void pidSlow(WPI_TalonSRX motor, double fire){
    setDistance = fire;
    kp = kc*0001;
    ki = kc*0001;
    e = error(fire);
    ei += e;
    motor.set(Fspeed-(java.lang.Math.abs(kp*e+ki*ei)));
    m_FLeft.set(Fspeed-(java.lang.Math.abs(kp*e+ki*ei)));
    // This method gradually slows down the motors 
  }

  public double error(double water) {
    setDistance = water;
    e = java.lang.Math.abs(setDistance)- distance(((java.lang.Math.abs(m_FRight.getSelectedSensorPosition()))*(java.lang.Math.abs(m_Left.getSelectedSensorPosition()))*(java.lang.Math.abs(m_Right.getSelectedSensorPosition())))/3);  
   if (water == 0){
      return 0.0;
   }
   if (water > 0){
    return e;
   }
   if (water < 0){
    e = -e;
    return e;
   }
   return 0.0;
  }

  public void moveX(double wall){
    e = error(wall);
    h = error(wall);
    // this method will run diffrent methods based on how close error is to 0
    ie = wall*0.75;   // sets inital arror eual to 75% of the desired distance 
    if (h > ie) { // if the error is greater than 75% of the intal error then call method pidSpeed graduall increasing speed
     // pidSpeed(m_FLeft,wall);
     // pidSpeed(m_FRight,wall);
      pidSpeed(m_Left,wall);
      //pidSpeed(m_Right,wall); 
      } 
    else { // if error is less than half of the intal offset
      if ( h > 1) {  // If error is greater than 1 inch call pidSlow gradually deacresing speed
       // pidSlow(m_FLeft,wall);
       // pidSlow(m_FRight,wall);
        pidSlow(m_Left,wall);
       // pidSlow(m_Right,wall);
      }
      else { // If error is less than one inch turn off motors.
        setMotors(0);
      }
     } 
    }
  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    moveX(y);
    // This changes the distance and it moves forwards
    /* if(distance(m_Left.getSelectedSensorPosition())<setDistance) {
      setMotors(0.1);
    }
    else {
      setMotors(-0.1);
    } */
    /* e = setDistance - distance(m_Left.getSelectedSensorPosition());
    // this sets e = to the error which is distance wanted - actual distance
    ei += e;// 
    setMotors(kp*e + ki*ei); /*this sets the motor value and 
    increses as it take longer to get error = to 0 */
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {}

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {}

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {}

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {}
}