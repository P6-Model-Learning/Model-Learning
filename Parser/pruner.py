class Pruner():
    def __int__(self):
        pass

    def badKTail(self, data: dict[list]):
        pruned_data = data

        in_sequence = []
        log_index = 0
        for boards in data:
            print("Checking for sequential entries on " + boards[0][0]["_HOSTNAME"])
            for traces in boards:
                for logs in traces:
                    trace1 = boards.index(traces)
                    msg1 = logs["MESSAGE"]
                    trace2 = log_index
                    msg2 = boards[log_index][0]["MESSAGE"]
                    if msg1 == msg2 and logs != boards[log_index][0]:
                        strt_msg = msg1
                        follows = 0
                        end_msg = ""
                        for i in range(len(traces) - 1):
                            msg11 = traces[i]["MESSAGE"]
                            msg22 = boards[log_index][i]["MESSAGE"]
                            if msg11 == msg22:
                                follows += 1
                                end_msg = msg11
                            else:
                                break
                        in_sequence.append(follows)
                        print(str(trace2) + " & " + str(trace1) + ". " + str(follows) + ":" + strt_msg + "->" + end_msg)
            log_index += 1
        print("smallest No of entries in sequence is " + str(min(in_sequence)) + " at index: " + str(in_sequence.index(min(in_sequence))+1))

    def prune(self, data: dict[list]):
        print("Pruning now")
        threshold = 2
        for board_traces in data:
            sequence_size = 0
            sequence_start = 0
            for trace in board_traces:
                while self.check_entry_identical(trace,
                                                 board_traces,
                                                 sequence_start + sequence_size):
                    sequence_size += 1
                    print(str(sequence_start))
                    if sequence_size >= threshold:
                        print("entries from " + str(sequence_start) + " to " +
                            str(sequence_start + sequence_size) +
                            " is identical for every trace")
                sequence_start = sequence_start + sequence_size + 1

    def check_entry_identical(self, current_trace, board_traces, entry_index):
        for trace in board_traces:
            if trace[entry_index]["MESSAGE"] == current_trace[entry_index]["MESSAGE"]:
                print(str(trace) + ":" + trace[entry_index]["MESSAGE"] + "|||||" + str(trace.key) + ":" + current_trace[entry_index]["MESSAGE"] + "\n") 
                return False
            previous_trace_entry = trace[entry_index]
        return True
