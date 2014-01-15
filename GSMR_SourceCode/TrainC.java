import java.util.LinkedList;

import EventPackage.Event;
import EventPackage.EventManagerInterface;
import EventPackage.EventQueue;
import InstrumentationPackage.MessageWindow;
import TermioPackage.Termio;

public class TrainC {
	private static class SBStatus {
        public boolean Status;
        int a;
        int speed1 = 0;
        String Location1 = "";
        float distance = 0.0f;
        String TrainBspeed ="";
        int TrainBSpeed = 0;
        String TrainMax = "";
        int TrainMaxSpeed = 0;
        LinkedList<String> Schedule = new LinkedList<String>();//new
        String route="";//new
        String id="9";
    }
	public static void main(String args[])
	{
		Termio UserInput = new Termio();
		String EvtMgrIP;					// Event Manager IP address
		EventManagerInterface em = null;	// Interface object to the event manager
		boolean Done = false;				// Loop termination flag
		String Option = "";				// Menu choice from user
		String speed = "";
		int i=0;
		int j=0;


		/////////////////////////////////////////////////////////////////////////////////
		// Get the IP address of the event manager
		/////////////////////////////////////////////////////////////////////////////////
		
		if ( args.length == 0 )
		{
		// event manager is on the local system
		
		System.out.println("\n\nAttempting to register on the local machine..." );
		
		try
		{
		// Here we create an event manager interface object. This assumes
		// that the event manager is on the local machine
		
		em = new EventManagerInterface();
		}
		
		catch (Exception e)
		{
		System.out.println("Error instantiating event manager interface: " + e);
		
		} // catch
		
		} else {
		
		// event manager is not on the local system
		
		EvtMgrIP = args[0];
		
		System.out.println("\n\nAttempting to register on the machine:: " + EvtMgrIP );
		
		try
		{
		// Here we create an event manager interface object. This assumes
		// that the event manager is NOT on the local machine
		
		em = new EventManagerInterface( EvtMgrIP );
		}
		
		catch (Exception e)
		{
		System.out.println("Error instantiating event manager interface: " + e);
		
		} // catch
		
		} // if
		
		// Here we check to see if registration worked. If ef is null then the
		// event manager interface was not properly created.
		if (em != null)
		{

			// We create a message window. Note that we place this panel about 1/2 across 
			// and 4/5s down the screen
			
			float WinPosX = 0.50f; 	//This is the X position of the message window in terms 
									//of a percentage of the screen height
			float WinPosY = 0.05f;	//This is the Y position of the message window in terms 
								 	//of a percentage of the screen height 
			
			MessageWindow mw = new MessageWindow("Train C Console", WinPosX, WinPosY);

			mw.WriteMessage("Registered with the GSM-R Communication Network of rail section 'A123'." );

	    	try
	    	{
				mw.WriteMessage("   Participant id: " + em.GetMyId() );
				mw.WriteMessage("   Registration Time: " + em.GetRegistrationTime() );

			} // try

	    	catch (Exception e)
			{
				mw.WriteMessage("Error:: " + e);

			} // catch
	    	
	    	mw.WriteMessage("\nTrain C is in the network 'A123'." );
	    	SBStatus s = new SBStatus();
	    	s.Status = true;
	    	s.a = 1;
	    	System.out.println("\nEnter train Configurations");
	    	System.out.println("Enter the Speed of the train");
	    	speed = UserInput.KeyboardReadString();
	    	s.speed1 = Integer.parseInt(speed);
	    	System.out.println("Enter the number of trains in the schedule");//new
	    	i = Integer.parseInt(UserInput.KeyboardReadString());//new
	    	System.out.println("Enter the schedule for the trains");//new
	    	for(j=0;j<i;j++)//new
	    	{
	    		s.Schedule.addLast(UserInput.KeyboardReadString());//new
	    	}
	    	System.out.println("Enter the route of this train");
	    	s.route=UserInput.KeyboardReadString();
	    	Thread startThread1 = new Thread(new Task1(s,em));
	    	startThread1.start();
	    	while(!Done)
	    	{  
	    		Thread startThread = new Thread(new Task(mw,em,s));
	    		startThread.start();
	    		
	    		if(s.distance > 1500)
	    		{
	    		if(s.Status == true)
	    		{
	    				s.a=2;	
	    		}
	    		if(s.distance >= 5000){
	    				s.a=3;
	    				Done = true;
	    				startThread1.stop();
	    		}
	    		}
	    		try
    			{
    				Thread.sleep(2000);

    			} // try

    			catch( Exception e )
    			{
    				mw.WriteMessage("Sleep error:: " + e );

    			} // catch
	    		startThread.stop();
	    	}//while
		}//if
	}//main
	
	private static class Task implements Runnable{
        MessageWindow mw2;
        EventManagerInterface em2;
        String msg1="Train C enters the rail network segment 'A123'";
        String msg2="Permission to take control of the swicth board";
        String msg3="Train C has relinquished control and has left the network";
        EventQueue eq = null;
        Event Evt = null;
        SBStatus s1 = new SBStatus();
        
       
        public Task( MessageWindow mw1,EventManagerInterface em1,SBStatus s){
            mw2 = mw1;
            em2 = em1;
            s1 = s;
        }
        
        @Override
        public void run() {
        	
        boolean Done1 = false;
   
        while(!Done1)
        {
        	
        	try
			{
				eq = em2.GetEventQueue();

			} // try

			catch( Exception e )
			{
				mw2.WriteMessage("Error getting event queue::" + e );

			} // catch
    		
    		int qlen = eq.GetSize();

			for ( int i = 0; i < qlen; i++ )
			{
				Evt = eq.GetEvent();

				if ( Evt.GetEventId() == 15 )
				{
					mw2.WriteMessage("Switch Board available");
				}
				if ( Evt.GetEventId() == 20 )
				{
					mw2.WriteMessage("Train A has switch board control");
					//s1.Status = false;
					//s1.a=0;
					s1.Status = true;
					s1.a = 1;
				}
				if ( Evt.GetEventId() == 21 )
				{
					//mw2.WriteMessage("Train B has switch board control");
					//s1.Status = true;
					//s1.a=1;
					mw2.WriteMessage("Train B has switch board control");
					s1.Status = true;
					s1.a = 1;
					//s1.Status = false;
				}
				if ( Evt.GetEventId() == 22)
				{
					mw2.WriteMessage("Train C has switch board control");
					s1.Status = false;
				}
				
				if ( Evt.GetEventId() == 25 )
				{
					mw2.WriteMessage("Switch Board Access denied");
				}
				if ( Evt.GetEventId() == 5 )
				{
					mw2.WriteMessage("Train A enters the rail network segment 'A123'");
				}
				if ( Evt.GetEventId() == 6 )
				{
					mw2.WriteMessage("Train B enters the rail network segment 'A123'");
				}
				if ( Evt.GetEventId() == 75 )
				{
					s1.TrainBspeed = Evt.GetMessage();
					s1.TrainBSpeed = Integer.parseInt(s1.TrainBspeed);
				}
				if ( Evt.GetEventId() == 76 )
				{
					s1.TrainMax = Evt.GetMessage();
					s1.TrainMaxSpeed = Integer.parseInt(s1.TrainMax);
				}
				
			}
        	if(s1.a == 1)
        	{
        		PostTrainStatus(em2,msg1,7);
        	}
        	if(s1.a == 2)
        	{
        		PostTrainStatus(em2,msg2,12);

        	}
        	if(s1.a == 3)
        	{
        		PostTrainStatus(em2,msg3,32); 
        	}
        	
        	try
			{
				Thread.sleep( 1000 );

			} // try

			catch( Exception e )
			{
				mw2.WriteMessage("Sleep error:: " + e );

			} // catch
        	
        }         
    } 
	}
	
	private static class Task1 implements Runnable{
 
        SBStatus s1 = new SBStatus();
        EventManagerInterface em2;
        public Task1(SBStatus s,EventManagerInterface em1){
          s1= s;
          em2 = em1;
        }
        
        @Override
        public void run() {
        //float SBdistance = 0.0f;
        float Speed = 0.0f;
        float WinPosX = 0.50f;
        float WinPosY = 0.25f;
        int lspeed = s1.speed1;
        MessageWindow mw2 = new MessageWindow("Train C Speed Monitor", WinPosX, WinPosY);
        System.out.println(lspeed);
        while(s1.distance < 5000)
        {
        	Speed = ((s1.speed1*5)/18);
            //System.out.println(Speed);
        	s1.distance = s1.distance + Speed;
        	mw2.WriteMessage("Train Speed : " + s1.speed1 + "Distance Covered : " + s1.distance);
        	if((s1.distance > 3000 && s1.Status == true && s1.speed1 > 0)||(s1.TrainBSpeed < s1.TrainMaxSpeed && s1.speed1 > 0)){
        		s1.speed1 = s1.speed1 - 20;
        	}
        	if((s1.distance > 3000 && s1.Status == false && s1.speed1 < lspeed)||(s1.TrainBSpeed == s1.TrainMaxSpeed && s1.speed1 == lspeed && s1.Status == true)||(s1.TrainBSpeed == s1.TrainMaxSpeed && s1.speed1 == lspeed && s1.Status == false)){
        		s1.speed1 = s1.speed1 + 20;
        	}
        	if(s1.distance<1500){
        		s1.Location1 = "InSection";
        		PostTrainStatus(em2,s1.Location1,45);
        		System.out.println(s1.Location1);
        	}
        	if(s1.distance>= 1500 && s1.distance < 3000){
        		s1.Location1 = "SBControl";
        		PostTrainStatus(em2,s1.Location1,45);
        		System.out.println(s1.Location1);
        	}
        	if(s1.distance >= 3000){
        		s1.Location1 = "Braking";
        		PostTrainStatus(em2,s1.Location1,45);
        		System.out.println(s1.Location1);
        	}
        	try
			{
				Thread.sleep(1000);

			} // try

			catch( Exception e )
			{
				mw2.WriteMessage("Sleep error:: " + e );

			} // catch
        	///if(SBdistance )
        }
    } 
	}
	static private void PostTrainStatus(EventManagerInterface ei, String msg1 ,int i)
	{
		// Here we create the event.

		Event evt = new Event( (int) i, msg1 );

		// Here we send the event to the event manager.

		try
		{
			ei.SendEvent( evt );

		} // try

		catch (Exception e)
		{
			System.out.println( "Error Posting Train Status:: " + e );

		} // catch

	} // PostTrainStatusEvent
	
}
