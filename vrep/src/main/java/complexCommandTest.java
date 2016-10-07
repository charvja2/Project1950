// Copyright 2006-2015 Coppelia Robotics GmbH. All rights reserved. 
// marc@coppeliarobotics.com
// www.coppeliarobotics.com
// 
// -------------------------------------------------------------------
// THIS FILE IS DISTRIBUTED "AS IS", WITHOUT ANY EXPRESS OR IMPLIED
// WARRANTY. THE USER WILL USE IT AT HIS/HER OWN RISK. THE ORIGINAL
// AUTHORS AND COPPELIA ROBOTICS GMBH WILL NOT BE LIABLE FOR DATA LOSS,
// DAMAGES, LOSS OF PROFITS OR ANY OTHER KIND OF LOSS WHILE USING OR
// MISUSING THIS SOFTWARE.
// 
// You are free to use/modify/distribute this file for whatever purpose!
// -------------------------------------------------------------------
//
// This file was automatically created for V-REP release V3.2.2 Rev1 on September 5th 2015

// This example illustrates how to execute complex commands from
// a remote API client. You can also use a similar construct for
// commands that are not directly supported by the remote API.
//
// Load the demo scene 'remoteApiCommandServerExample.ttt' in V-REP, then 
// start the simulation and run this program.
//
// IMPORTANT: for each successful call to simxStart, there
// should be a corresponding call to simxFinish at the end!

import coppelia.IntW;
import coppelia.IntWA;
import coppelia.FloatWA;
import coppelia.CharWA;
import coppelia.remoteApi;

public class complexCommandTest
{
	public static CharWA getCmdString(int id,int cnt,CharWA data)
	{
		int l=12+data.getLength();
		IntWA header=new IntWA(3);
		header.getArray()[0]=id;
		header.getArray()[1]=cnt;
		header.getArray()[2]=l;
		char[] a=header.getCharArrayFromArray();
		CharWA retVal=new CharWA(l);
		System.arraycopy(a,0,retVal.getArray(),0,a.length);
		System.arraycopy(data.getArray(),0,retVal.getArray(),a.length,data.getLength());
		return(retVal);
	}

	public static CharWA waitForCmdReply(remoteApi vp,int clId,int cnt)
	{
		while (true)
		{
			CharWA str=new CharWA(0);
			int result=vp.simxReadStringStream(clId,"repliesToRemoteApiClient",str,vp.simx_opmode_streaming);
			if (result==vp.simx_return_ok)
			{
				while (str.getLength()>0)
				{
					IntWA header=new IntWA(1);
					header.initArrayFromCharArray(str.getArray());
					if (cnt==header.getArray()[1])
					{
						CharWA replyData=new CharWA(0);
						if (header.getArray()[2]>12)
						{
							replyData=new CharWA(header.getArray()[2]-12);
							System.arraycopy(str.getArray(),12,replyData.getArray(),0,replyData.getLength());
						}
						return(replyData);
					}
					if (header.getArray()[2]>=str.getLength())
						str.initArray(0);
					else
					{
						char[] tmp=str.getArray();
						str.initArray(tmp.length-header.getArray()[2]);
						System.arraycopy(tmp,header.getArray()[2],str.getArray(),0,str.getLength());
					}
				}
			}
		}
	}
	
	public static void main(String[] args)
	{
		System.out.println("Program started");
		remoteApi vrep = new remoteApi();
		vrep.simxFinish(-1); // just in case, close all opened connections
		int clientID = vrep.simxStart("127.0.0.1",19999,true,true,5000,5);
		if (clientID!=-1)
		{
			System.out.println("Connected to remote API server");	

			// Commands are send via the string signal 'commandsFromRemoteApiClient'.
			// Commands are simply appended to that string signal
			// Each command is coded in following way:
			// 1. Command ID (integer, 4 chars)
			// 2. Command counter (integer, 4 chars). Simply start with 0 and increment for each command you send
			// 3. Command length (integer, includes the command ID, the command counter, the command length, and the additional data (i.e. command data))
			// 4. Command data (chars, can be of any length, depending on the command)
			//
			// Replies are coded in a same way. An executed command should reply with the same command counter.
			// 
			// Above simple protocol is just an example: you could use your own protocol! (but it has to be the same on the V-REP side)
		
			// 1. First send a command to display a specific message in a dialog box:
			int cmdID=1; // this command id means: 'display a text in a message box'
			int cmdCnt=0;
			CharWA cmdData=new CharWA("Hello world!");
			CharWA stringToSend=getCmdString(cmdID,cmdCnt,cmdData);
			vrep.simxWriteStringStream(clientID,"commandsFromRemoteApiClient",stringToSend,vrep.simx_opmode_oneshot);
			System.out.format("Returned message: %s\n",waitForCmdReply(vrep,clientID,cmdCnt).getString()); // display the reply from V-REP (in this case, just a string)

			// 2. Now create a dummy object at coordinate 0.1,0.2,0.3:
			cmdID=2; // this command id means: 'create a dummy at a specific coordinate with a specific name'
			cmdCnt++;
			CharWA dummyName=new CharWA("MyDummyName");
			FloatWA coords=new FloatWA(3);
			coords.getArray()[0]=0.1f;
			coords.getArray()[1]=0.2f;
			coords.getArray()[2]=0.3f;
			char[] b=coords.getCharArrayFromArray();
			cmdData=new CharWA(dummyName.getLength()+b.length);
			System.arraycopy(dummyName.getArray(),0,cmdData.getArray(),0,dummyName.getLength());
			System.arraycopy(b,0,cmdData.getArray(),dummyName.getLength(),b.length);
			stringToSend=getCmdString(cmdID,cmdCnt,cmdData);
			vrep.simxWriteStringStream(clientID,"commandsFromRemoteApiClient",stringToSend,vrep.simx_opmode_oneshot);
			IntWA intVal=new IntWA(0);
			intVal.initArrayFromCharArray(waitForCmdReply(vrep,clientID,cmdCnt).getArray());
			System.out.format("Dummy handle: %d\n",intVal.getArray()[0]); // display the reply from V-REP (in this case, the handle of the created dummy)
			
			// Now close the connection to V-REP:	
			vrep.simxFinish(clientID);
		}
		else
			System.out.println("Failed connecting to remote API server");
		System.out.println("Program ended");
	}
}
			
