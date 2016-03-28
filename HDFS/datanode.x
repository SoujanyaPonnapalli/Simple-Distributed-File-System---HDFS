program DATANODE{
    version DN{
		/* ReadBlockResponse readBlock(ReadBlockRequest)) */
	/* Method to read data from any block given block-number */
        string readBlock(string protobuf<>) = 6;
		
		/* WriteBlockResponse writeBlock(WriteBlockRequest) */
	/* Method to write data to a specific block */
        string writeBlock(string protobuf<>) = 7;
    } = 1;

} = 0x20110001 ;

