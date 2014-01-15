import EventPackage.Event;
import EventPackage.EventManagerInterface;
import EventPackage.EventQueue;
import InstrumentationPackage.MessageWindow;
import TermioPackage.Termio;
public class SwitchBoard {
	public static void main(String args[])
	{
		//Termio UserInput = new Termio();
		String EvtMgrIP;					// Event Manager IP address
		EventManagerInterface em = null;	// Interface object to the event manager
		boolean Done = false;				// Loop termination flag
		//String Option = "";				// Menu choice from user
		boolean SBStatus = false;
		Event Evt = null;					// Event object
		EventQueue eq = null;				// Message Queue
		//int EvtId = 0;						// User specified event ID
		int a=0;
        String msg1 = "Switch Board Available!";
        String msg2 = "Train A has swicthboard Control";
        String msg3 = "Switch Board Access Denied!";
        String msg4 = "Train B has switchboard Control";
        

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
			float WinPosY = 0.45f;	//This is the Y position of the message window in terms 
								 	//of a percentage of the screen height 
			
			MessageWindow mw = new MessageWindow("Switch Board Console", WinPosX, WinPosY);

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
	    	
	    	//mw.WriteMessage("\nTrain A is in the network 'A123'." );
	    	
	    	while(!Done)
	    	{  
	    		if(SBStatus == false){
	    			PostSwitchBoardStatus(em, msg1 ,15);	
	    			mw.WriteMessage("Switch Board Available");
	    		}
	    		
	    		try
				{
					eq = em.GetEventQueue();

				} // try

				catch( Exception e )
				{
					mw.WriteMessage("Error getting event queue::" + e );

				} // catch
	    		
	    		int qlen = eq.GetSize();

				for ( int i = 0; i < qlen; i++ )
				{
					Evt = eq.GetEvent();

					if ( Evt.GetEventId() == 10 )
					{
						if(SBStatus == false || a == 1)
						{
						 PostSwitchBoardStatus(em, msg2 ,20);
						 mw.WriteMessage("Train A has switch board control");
						 SBStatus = true;
						 a=1;
						}else{
							PostSwitchBoardStatus(em, msg3 ,25);
						}
					}
					
					if ( Evt.GetEventId() == 11 )
					{
						if(SBStatus == false || a == 2 )
						{
						 PostSwitchBoardStatus(em, msg4 ,21);
						 mw.WriteMessage("Train B has switch board control");
						 SBStatus = true;
						 a=2;
						
						}else{
							PostSwitchBoardStatus(em, msg3 ,26);
						}
					}
					

					if ( Evt.GetEventId() == 30 )
					{
						SBStatus = false;
						a=0;
						mw.WriteMessage("Train A has relinquished the switch board control");
					}
					
					if ( Evt.GetEventId() == 31 )
					{
						SBStatus = false;
						a=0;
						mw.WriteMessage("Train B has relinquished the switch board control");
					}
					if ( Evt.GetEventId() == 5 )
					{
						mw.WriteMessage("Train A enters the rail network segment 'A123'");
					}
					if ( Evt.GetEventId() == 6 )
					{
						mw.WriteMessage("Train B enters the rail network segment 'A123'");
					}
				}
	    	    
				try
				{
					Thread.sleep( 500 );

				} // try

				catch( Exception e )
				{
					mw.WriteMessage("Sleep error:: " + e );

				} // catch
	        	
	    	}//while
		}//if
	}//main
	static private void PostSwitchBoardStatus(EventManagerInterface ei, String msg1 ,int i)
	{
		// Here we create the event.

		Event evt = new Event( (int) i, msg1 );

		// Here we send the event to the event manager.

		try
		{
			ei.SendEvent( evt );
			//mw.WriteMessage( "Sent WindowSensor Event" );

		} // try

		catch (Exception e)
		{
			System.out.println( "Error Posting Train Status:: " + e );

		} // catch

	} // PostSwitchBoardEvent	
}
