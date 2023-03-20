import os
from systemd import journal
import json

class JournalParser:
    def getBoards(self):
        pass

    def parse(self):
        data = []
        for root, dirs, files in os.getcwd:
            for file in files:
                if file.endswitch('.journal'):
                    trace = []
                    j = journal.Reader(path=root)
                    j.get_next(skip=1)
                    j.log_level(level=7)
                    for entry in j:
                        trace.append(entry)
                    data.append(trace)
        return data
    
    def parseToJSON(self, data):
        return json.dumps(data)
