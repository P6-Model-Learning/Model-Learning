import os
from systemd import journal
import json

class JournalParser:
    startStr = ""
    endStr = ""

    def getBoards(self):
        pass

    def parse(self):
        data = []
        startTime = None
        for root, dirs, files in os.walk(os.getcwd()):
            for file in files:
                if file.endswith('.journal'):
                    trace = []
                    j = journal.Reader(path=root)
                    j.get_next(skip=1)
                    j.log_level(level=7)
                    for entry in j:
                        if entry['MESSAGE'] == self.startStr:
                            trace.append(entry)
                    data.append(trace)
        return data
    
    def parseToJSON(self, data):
        return json.dumps(data, sort_keys=True, default=str)
