program JOBTRACKER{
    version JT{
	/* JobSubmitResponse jobSubmit(JobSubmitRequest) */
	string jobSubmit(string protobuf<>) = 1;

	/* JobStatusResponse getJobStatus(JobStatusRequest) */
	string getJobStatus(string protobuf<>) = 2;
	
	/* HeartBeatResponse heartBeat(HeartBeatRequest) */
	string heartBeat(string protobuf<>) = 3;
    } = 1;

} = 0x20110002 ;

