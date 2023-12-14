// closeLoopPosControl+SequAuto                  Robot.j
// from REV closeLoop example
// This version add sequential auto cmd, and manual stick control in teleOp in 
// parallel with SD controlled PID position. all OK w/ one motor bench test.
// demo use of Cmd/Subsys elements along with position PID control in auto and
// teleop, along with manual teleOp (subsys method reading joystick axis) ==>
// flat framewk, multi-mode control, and Cmd/Subsys do not conflict here 

// 1st mod tested PID position on one NEO motor;
// tuned param for unloaded Neo rotation 10-100 ; no manual control, no RC,
// OI, constant class                                                         
// 2nd +auto version single autoComd w/ pos, SmtDash for var display, isFin() 
// of cmd, user control of tele pos. All working == finalize; some Cmd/Subsys
// use but all config in Robot.j
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
  public static DriveSubsys _myDrive = new DriveSubsys();
  public static Joystick _myJoy = new Joystick(0);

  // declare some class var here
  // specify optional auto cmd's in roboInit
  // schedule auto choice in autoInit
  private SendableChooser<Command> _autonChooser;

  private Command _simpleAuto;
  private Command _simpleBak;
  private SequentialCommandGroup _autoSequ1;

  // private Command _autoComm;
  private Command _teleComd;
  private double _driveSetting; // <-- teleOp gets set point input from user
  public static double autoDriveInch = 42; // how far to auto, all cmd use

  @Override
  public void robotInit() {
    // left this var as inch but actually rotations in this bench test
    _simpleAuto = new GoToPosition(autoDriveInch);
    _simpleBak = new GoToPosition(-autoDriveInch); // go backward same amt
    _autoSequ1 = new autoFwdRotBak();

    // for SmtDashbd setting auto choice:
    _autonChooser = new SendableChooser<>();
    _autonChooser.setDefaultOption("goFwd42", _simpleAuto);
    _autonChooser.addOption("goBack42", _simpleBak);
    _autonChooser.addOption("fwd-wait-bak", _autoSequ1);

    // // sending data to chooser ---
    // // may require NetworkTableInstance.getDefault();
    // if SmtDbd not enabled, does this appear in LV-dash's chooser? NO
    SmartDashboard.putData("Auton Selector", _autonChooser);

    System.out.println("robot initialized");

    // display PID coefficients (from subsys import) on SmartDashboard
    SmartDashboard.putNumber("P Gain", kP);
    SmartDashboard.putNumber("I Gain", kI);
    SmartDashboard.putNumber("D Gain", kD);
    SmartDashboard.putNumber("I Zone", kIz);
    SmartDashboard.putNumber("Feed Forward", kFF);
    SmartDashboard.putNumber("Max Output", kMaxOutput);
    SmartDashboard.putNumber("Min Output", kMinOutput);

    // to set desired set point position & show actual encoder value
    SmartDashboard.putNumber("driveSetting", 0);
    SmartDashboard.putNumber("encodValue", 0);

    SmartDashboard.putString("GTPcmd fin?", "???");
    // for manual teleop do i need _myDrive.setDefaultCommand(manualDrvCmd)?
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

    // if PID coeffic on SmartDash have changed, write new value to 
    // to static var, so reset in subsys as well ?
    if ((p != kP)) {
      _leftPIDControl.setP(p);
      kP = p;
    }
    if ((i != kI)) {
      _leftPIDControl.setI(i);
      kI = i;
    }
    if ((d != kD)) {
      _leftPIDControl.setD(d);
      kD = d;
    }
    if ((iz != kIz)) {
      _leftPIDControl.setIZone(iz);
      kIz = iz;
    }
    if ((ff != kFF)) {
      _leftPIDControl.setFF(ff);
      kFF = ff;
    }
    if ((max != kMaxOutput) || (min != kMinOutput)) {
      _leftPIDControl.setOutputRange(min, max);
      kMinOutput = min;
      kMaxOutput = max;
    }
    // SmartDashboard.putNumber("RotatSetting", rotations);
    SmartDashboard.putNumber("encodValue", _leftEncoder.getPosition());
    CommandScheduler.getInstance().run();
  } // end roboPeri

  @Override
  public void autonomousInit() {
    // rezero encoder reading every auto startup
    _leftEncoder.setPosition(0);

    // get a chooser option from those put on SmtDash by roboInit
    // sequential group cmd executes fully from the one scheduler call here
    if (_autonChooser.getSelected() != null) {
      _autonChooser.getSelected().schedule();
    }

    // for LV DB chooser: works but DS keeps whatever 1st init cmd it gets
    // from DB, does not refresh from LV DB, no fix found
    // String autoChoice = SmartDashboard.getString("Auto List", "drive Bk");
    // // would make drive Bk the default choice
    // switch (autoChoice) {// _autoArray = {"drive Forward", "drive Back",
    // "fwdRotBakSeq"};
    // case "drive Forward":
    // // set cmd here _autoCmd = new myDrvFwdCmd(); break;
    // _simpleAuto.schedule(); break;
    // case "drive Back":
    // // other choice _autoCmd = new myDrvBakCmd(); break;
    // _simpleBack.schedule();
    // case "fwdRotBakSeq":
    // _autoSequ1.schedule();
    // break;
    // } // end switch

  } // end autoInit

  @Override
  public void autonomousPeriodic() {
    // super class method of subsyst may do something here,
    // but seems like autoInit runs seqCmdGroup as well
  }

  @Override
  public void teleopInit() {
    // rezero encoder reading every teleop start
    _leftEncoder.setPosition(0);
    _driveSetting = SmartDashboard.getNumber("driveSetting", 0.0);
    _teleComd = new GoToPosition(_driveSetting);
    _teleComd.schedule(); // needs to be here for one-off cmd to work
  } // end teleopInit

  @Override
  public void teleopPeriodic() {
    SmartDashboard.putNumber("encodValue", _leftEncoder.getPosition());
    // this worked, used rot/drive var set by SD field, motor class's method;
    // but bypasses GTP cmd, so no isFin evaluated
    // _leftPIDControl.setReference(_driveSetting,
    // CANSparkMax.ControlType.kPosition);
    // use GTP instead (v.i.) to assure position finish occured.
    // can I add manual joy drive method too? Y, I put motor class method
    // in subsys's periodic() and works in parallel with position PID control
  } // end teleoPeriod

  @Override
  public void disabledInit() {
    // needed to reset sticky var in code, problematic on repeat cmd call
    // mainly a problem SD displaying old values
    _leftEncoder.setPosition(0);

    // refresh PID coefficient (from current code var) on SmartDashboard
    SmartDashboard.putNumber("P Gain", kP);
    SmartDashboard.putNumber("I Gain", kI);
    SmartDashboard.putNumber("D Gain", kD);
    SmartDashboard.putNumber("I Zone", kIz);
    SmartDashboard.putNumber("Feed Forward", kFF);
    SmartDashboard.putNumber("Max Output", kMaxOutput);
    SmartDashboard.putNumber("Min Output", kMinOutput);
    // set desired position & show actual encod value (should be 0)
    SmartDashboard.putNumber("driveSetting", 0);
    SmartDashboard.putNumber("encodValue", _leftEncoder.getPosition());
    SmartDashboard.putString("GTPcmd fin?", "???");
  }
} // end robo.j
