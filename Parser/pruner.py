class Pruner():
    def __int__(self):
        pass

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
            if trace[entry_index]["MESSAGE"] != current_trace[entry_index]["MESSAGE"]:
                print(trace[entry_index]["MESSAGE"] + "|||||" + current_trace[entry_index]["MESSAGE"] + "\n") 
                return False
            previous_trace_entry = trace[entry_index]
        return True
