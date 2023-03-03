class Pruner():
    def __int__(self):
        pass

    def prune(self, data: dict[list]):
        print("Pruning now")
        previous_trace = data[0]
        # Do the pruuning
        i = 0
        for trace in data[1:]:
            for entry in trace:
                entry_message = entry["MESSAGE"]
                if entry_message == previous_trace[i]["MESSAGE"]:
                    print(entry_message + " (from trace" + str(i) + ")" +
                          " | == | " + previous_trace[i]["MESSAGE"] +
                          " (from trace" + str(i - 1) + ")")
            i += 1
            previous_trace = trace
