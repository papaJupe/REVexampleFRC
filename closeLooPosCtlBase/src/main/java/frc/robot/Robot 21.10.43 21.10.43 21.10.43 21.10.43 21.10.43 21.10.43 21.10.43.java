// CloseLooPosCtlBase                     Robot.j
// v. 1
// orig REV closeLoop example, mod here to test PID position control on one
// NEO motor; tuned PID constants for unloaded Neo, rotation 10-100;
// interactive SmartDashbd displays current values, user can change, rerun
// to see effect; all code is in this one class.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class Robot extends TimedRobot {
  private static final int deviceID = 13;
  private CANSparkMax m_motor;
  private SparkMaxPIDController m_pidController;
  private RelativeEncoder m_encoder;
  public double kP, kI, kD, kIz, kFF, kMaxOutput, kMinOutput;

  @Override
  public void robotInit() {
    // initialize motor
    m_motor = new CANSparkMax(deviceID, MotorType.kBrushless);

    /* restoreFactoryDefaults method will reset the configuration parameters
     * in the SPARK MAX to their factory default state. 
     * If no argument is passed, these
     * parameters will not persist between power cycles
     */
   // m_motor.restoreFactoryDefaults();

    /* to use PID functionality for a controller, a SparkMaxPIDController
    * object is constructed by calling the getPIDController() method on an
    * existing CANSparkMax object
    */
    m_pidController = m_motor.getPIDController();

    // Encoder object created to display position values for an existing 
    // CANSparkMax object
    m_encoder = m_motor.getEncoder(); // as is, getPosition outputs rotations
    // need to enter setPositionConversionFactor() for your mechanism
    // for the encoder to give position units of choice

    // PID coefficients, free running w/o load
    kP = 0.15; 
    kI = 2e-6;
    kD = 8; 
    kIz = 8; 
    kFF = 0; 
    kMaxOutput = 0.1; 
    kMinOutput = -0.1;

    // set PID coefficients
    m_pidController.setP(kP);
    m_pidController.setI(kI);
    m_pidController.setD(kD);
    m_pidController.setIZone(kIz);
    m_pidController.setFF(kFF);
    m_pidController.setOutputRange(kMinOutput, kMaxOutput);

    // display PID coefficients on SmartDashboard
    SmartDashboard.putNumber("P Gain", kP);
    SmartDashboard.putNumber("I Gain", kI);
    SmartDashboard.putNumber("D Gain", kD);
    SmartDashboard.putNumber("I Zone", kIz);
    SmartDashboard.putNumber("Feed Forward", kFF);
    SmartDashboard.putNumber("Max Output", kMaxOutput);
    SmartDashboard.putNumber("Min Output", kMinOutput);
    // to set desired position
    SmartDashboard.putNumber("RotatSetting", 0);
    // to show actual encod value
    SmartDashboard.putNumber("encodeValue", m_encoder.getPosition());

  }  // end roboInit

  @Override
  public void teleopInit() {
 // rezero encoder reading every teleop startup
     m_encoder.setPosition(0);
  }  // end teleopInit


  @Override
  public void teleopPeriodic() {
    // read PID coeffic & setpoint from SmtDashbd --> can change prn
    double p = SmartDashboard.getNumber("P Gain", 0);
    double i = SmartDashboard.getNumber("I Gain", 0);
    double d = SmartDashboard.getNumber("D Gain", 0);
    double iz = SmartDashboard.getNumber("I Zone", 0);
    double ff = SmartDashboard.getNumber("Feed Forward", 0);
    double max = SmartDashboard.getNumber("Max Output", 0);
    double min = SmartDashboard.getNumber("Min Output", 0);
    double rotations = SmartDashboard.getNumber("RotatSetting", 0);

    // if PID coeffic || set pt on SmtDash changed, write new val to
      //    controller
    if((p != kP)) { m_pidController.setP(p); kP = p; }
    if((i != kI)) { m_pidController.setI(i); kI = i; }
    if((d != kD)) { m_pidController.setD(d); kD = d; }
    if((iz != kIz)) { m_pidController.setIZone(iz); kIz = iz; }
    if((ff != kFF)) { m_pidController.setFF(ff); kFF = ff; }
    if((max != kMaxOutput) || (min != kMinOutput)) { 
      m_pidController.setOutputRange(min, max); 
      kMinOutput = min; kMaxOutput = max; 
    }

    /* PIDController objects are commanded to a set point using the 
     * setReference() method.  
     * The first parameter is the value of the set point, whose units vary
     * depending on the control type set in the second parameter.
     * 
     * The second parameter is the control type :
     *  com.revrobotics.CANSparkMax.ControlType.kDutyCycle
     *  com.revrobotics.CANSparkMax.ControlType.kPosition
     *  com.revrobotics.CANSparkMax.ControlType.kVelocity
     *  com.revrobotics.CANSparkMax.ControlType.kVoltage
     */
  // command rotation set pt. to input from SmtDash (above), 
  // then redisplay to SmtDash, along with encod value now
  m_pidController.setReference(rotations,CANSparkMax.ControlType.kPosition);
    
    SmartDashboard.putNumber("RotatSetting", rotations);
    SmartDashboard.putNumber("encodeValue", m_encoder.getPosition());
  } // end teleoPeriod

}  // end robot.j
