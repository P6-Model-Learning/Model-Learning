import os
from systemd import journal
import json

class JournalParser:
    startStr = "Test start line inserted"
    endStr = "Active journal rotated"

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
                            startTime = entry['_SOURCE_MONOTONIC_TIMESTAMP']
                            trace.append(entry)
                        elif startTime != None:
                            trace.append(entry)
                    data.append(trace)
                    startTime = None
        return data
    
    def parseSimple(self):
        data = []
        start_time = None
        for root, dirs, files in os.walk(os.getcwd()):
            for file in files:
                if file.endswith('.journal'):
                    trace = []
                    j = journal.Reader(path=root)
                    j.get_next(skip=1)
                    j.log_level(level=7)
                    for entry in j:
                        if entry['MESSAGE'] == self.startStr:
                            start_time = entry['_SOURCE_MONOTONIC_TIMESTAMP']
                            trace.append({
                                entry['_SOURCE_MONOTONIC_TIMESTAMP'],
                                entry['MESSAGE'],
                                entry['_HOSTNAME']
                            })
                        elif start_time != None:
                            trace.append({
                                entry['_SOURCE_MONOTONIC_TIMESTAMP'],
                                entry['MESSAGE'],
                                entry['_HOSTNAME']
                            })
                    data.append(trace)
                    start_time = None
        return data
    
    def getBoards(self) -> list:
        boards = []

        for root, dirs, files in os.walk(os.getcwd()):
            if root not in boards and (os.path.basename(root)).endswith('.prevas.dk'):
                boards.append(((os.path.basename(root))
                               .split('-dut')[0], os.path.relpath(root, os.getcwd())))
        return boards
    
    def parseToJSON(self, data):
        return json.dumps(data, sort_keys=True, default=str)
