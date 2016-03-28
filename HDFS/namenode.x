program NAMENODE{
    version NN{
		/* OpenFileResponse openFile(OpenFileRequest) */
	/* Method to open a file given file name with read-write flag*/
        string openFile(string protobuf<>) = 1;
		
		/* BlockLocationResponse getBlockLocations(BlockLocationRequest) */
	/* Method to get block locations given an array of block numbers */
        string getBlockLocations(string protobuf<>) = 2;

		/* AssignBlockResponse assignBlock(AssignBlockRequest) */
	/* Method to assign a block which will return the replicated block locations */
        string assignBlock(string protobuf<>) = 3;
		
		/* CloseFileResponse closeFile(CloseFileRequest) */
        string closeFile(string protobuf<>) = 4;
		
		/* ListFilesResponse list(ListFilesRequest) */
	/* List the file names (no directories needed for current implementation */
        string list(string protobuf<>) = 5;

		/* BlockReportResponse blockReport(BlockReportRequest) */
	/* Get the status for blocks */
        string sendBlockReport(string protobuf<>) = 8;
		
		/* HeartBeatResponse heartBeat(HeartBeatRequest) */
	/* Heartbeat messages between NameNode and DataNode */
        string sendHeartBeat(string protobuf<>) = 9;
    } = 1;

} = 0x20110000 ;

