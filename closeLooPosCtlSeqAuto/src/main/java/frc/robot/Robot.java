// closeLoopPosCtlSeqAuto                          Robot.j
// v. 3 mod from REV closeLoop example

// this added sequential auto cmd, stick control of position in teleOp, 
// in parallel with SmtDash controlled live PID config and position set. 
// demos using Cmd/Subsys elements in simple flat framework, no RoboCont.  

// v.1 set PID position on one NEO motor; user could tune PID param
// for unloaded Neo, rotation 10-100 ; no manual control, RC, OI, Constant
                                                         
// v.2 added one autoComd (set position), or user can set pos. in teleOp

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

import static frc.robot.DriveSubsys.*;

public class Robot extends TimedRobot {
  // single CANSparkMax instanced in subsys
  public static DriveSubsys myDrive = new DriveSubsys();
  public static Joystick myJoy = new Joystick(0);

  // declare some class var here
  // specify optional auto cmd's in roboInit
  // schedule auto choice in autoInit
  private SendableChooser<Command> autonChooser;

  private Command simpleAuto;
  private Command simpleBak;
  private SequentialCommandGroup autoSequ1;

  // private Command autoComm;
  private Command teleComd;
  private double driveSetting; // <-- teleOp gets set point input from user
  public static double autoDriveInch = 42; // how far to auto, all cmd use

  @Override
  public void robotInit() {
    // left this var as inch but actually rotations in this bench test
    simpleAuto = new GoToPosition(autoDriveInch);
    simpleBak = new GoToPosition(-autoDriveInch); // go backward same amt
    autoSequ1 = new autoFwdRotBak();

    // for SmtDashbd to set auto choice:
    autonChooser = new SendableChooser<>();
    autonChooser.setDefaultOption("goFwd42", simpleAuto);
    autonChooser.addOption("goBack42", simpleBak);
    autonChooser.addOption("fwd-wait-bak", autoSequ1);

    // // sending data to chooser ---
    // // may require NetworkTableInstance.getDefault();
    // if SmtDbd not enabled, does this appear in LV-dash's chooser? NO
    SmartDashboard.putData("Auton Selector", autonChooser);

    System.out.println("robot initialized");

    // display PID coefficients (from subsys import) on SmartDashboard
    SmartDashboard.putNumber("P Gain", kP);
    SmartDashboard.putNumber("I Gain", kI);
    SmartDashboard.putNumber("D Gain", kD);
    SmartDashboard.putNumber("I Zone", kIz);
    SmartDashboard.putNumber("Feed Forward", kFF);
    SmartDashboard.putNumber("Max Output", kMaxOutput);
    SmartDashboard.putNumber("Min Output", kMinOutput);

    // init field for desired position set point
    SmartDashboard.putNumber("driveSetting", 0);
    // to show actual encoder value
    SmartDashboard.putNumber("encodValue", 0);

    SmartDashboard.putString("GTPcmd fin?", "???");
    // for manual teleop do i need myDrive.setDefaultCommand(manualDrvCmd)?
    // no, v. DriveSubsys method for manual drive method
  } // end roboInit

  @Override
  public void robotPeriodic() { // to read current PID coeffic from SmtDashbd
    double p = SmartDashboard.getNumber("P Gain", 0);
    double i = SmartDashboard.getNumber("I Gain", 0);
    double d = SmartDashboard.getNumber("D Gain", 0);
    double iz = SmartDashboard.getNumber("I Zone", 0);
    double ff = SmartDashboard.getNumber("Feed Forward", 0);
    double max = SmartDashboard.getNumber("Max Output", 0);
    double min = SmartDashboard.getNumber("Min Output", 0);

    // if PID coeffic on SmartDash have changed, write new value  
    // to static var, so should reset value in subsys as well ?
    if ((p != kP)) {
      leftPIDControl.setP(p);
      kP = p;
    }
    if ((i != kI)) {
      leftPIDControl.setI(i);
      kI = i;
    }
    if ((d != kD)) {
      leftPIDControl.setD(d);
      kD = d;
    }
    if ((iz != kIz)) {
      leftPIDControl.setIZone(iz);
      kIz = iz;
    }
    if ((ff != kFF)) {
      leftPIDControl.setFF(ff);
      kFF = ff;
    }
    if ((max != kMaxOutput) || (min != kMinOutput)) {
      leftPIDControl.setOutputRange(min, max);
      kMinOutput = min;
      kMaxOutput = max;
    }
    // SmartDashboard.putNumber("RotatSetting", rotations);
    SmartDashboard.putNumber("encodValue", leftEncoder.getPosition());
    CommandScheduler.getInstance().run();
  } // end roboPeri

  @Override
  public void autonomousInit() {
    // rezero encoder reading every auto startup
    leftEncoder.setPosition(0);

    // get a chooser option from those put on SmtDash by roboInit
    // sequential group cmd executes fully from the one scheduler call here
    if (autonChooser.getSelected() != null) {
      autonChooser.getSelected().schedule();
    }

    // for LV DB chooser: works but DS keeps whatever 1st init cmd it gets
    // from DB, does not refresh from LV DB, no fix found
    // String autoChoice = SmartDashboard.getString("Auto List", "drive Bk");
    // // would make drive Bk the default choice
    // switch (autoChoice) {// autoArray = {"drive Forward", "drive Back",
    // "fwdRotBakSeq"};
    // case "drive Forward":
    // // set cmd here autoCmd = new myDrvFwdCmd(); break;
    // simpleAuto.schedule(); break;
    // case "drive Back":
    // // other choice autoCmd = new myDrvBakCmd(); break;
    // simpleBack.schedule();
    // case "fwdRotBakSeq":
    // autoSequ1.schedule();
    // break;
    // } // end switch

  } // end autoInit

  @Override
  public void autonomousPeriodic() {
    // super class method of subsyst may do something here,
    // but seems like autoInit runs seqCmdGroup as it does others
  }

  @Override
  public void teleopInit() {
    // rezero encoder reading every teleop start
    leftEncoder.setPosition(0);
    // get new desired position value
    driveSetting = SmartDashboard.getNumber("driveSetting", 0.0);
    teleComd = new GoToPosition(driveSetting);
    teleComd.schedule(); // needs to be here for one-off cmd to work
  } // end teleopInit

  @Override
  public void teleopPeriodic() {
    SmartDashboard.putNumber("encodValue", leftEncoder.getPosition());
    // this worked, used rot/drive var set by SD field, motor class's method;
    // but bypasses GTP cmd, so isFin() not evaluated
    // leftPIDControl.setReference(driveSetting,
    //             CANSparkMax.ControlType.kPosition);
    // I use GTP instead (v.i.) to assure position cmd finish occured.
    // can I add manual joy drive method too? Y, I put motor class method
    // in subsys's periodic(), works alongside SmtDash position control
  } // end teleoPeriod

  @Override
  public void disabledInit() {
    // needed to reset sticky var in code, problematic on repeat cmd call
    // mainly a problem SD displaying old values
    leftEncoder.setPosition(0);

    // refresh PID coefficient (from current code var) on SmartDashboard
    SmartDashboard.putNumber("P Gain", kP);
    SmartDashboard.putNumber("I Gain", kI);
    SmartDashboard.putNumber("D Gain", kD);
    SmartDashboard.putNumber("I Zone", kIz);
    SmartDashboard.putNumber("Feed Forward", kFF);
    SmartDashboard.putNumber("Max Output", kMaxOutput);
    SmartDashboard.putNumber("Min Output", kMinOutput);
    // set desired position 
    SmartDashboard.putNumber("driveSetting", 0);
    // show actual encod value (should be 0)
    SmartDashboard.putNumber("encodValue", leftEncoder.getPosition());
    SmartDashboard.putString("GTPcmd fin?", "???");
  }
} // end robot.j
