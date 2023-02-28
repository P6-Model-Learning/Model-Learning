import os
import fnmatch
from systemd import journal


class Reader:
    def __int__(self):
        pass

    def read_journals(self) -> list:
        syslog_list = []
        for root, dirs, files in os.walk(os.pardir):
            path = root.split(os.path.sep)
            for file in files:
                if file.startswith('system.journal') and file.endswith('.journal'):
                    j = journal.Reader(path=root)
                    j.get_next(skip=1)
                    j.log_level(level=7)
                    j.add_match("SYSLOG_IDENTIFIER=systemd")
                    for entry in j:
                        syslog_list.append(entry)
                        pass  # TODO: parsing <3
        return syslog_list

    def print_dir(self):
        for root, dirs, files in os.walk(os.pardir):
            path = root.split(os.sep)
            for file in files:
                if file.startswith("system.journal") and file.endswith(".journal"):
                    print((len(path) - 1) * '---', os.path.basename(root))
                    print((len(path) - 1) * '   ' + ' |__', file)

#test
reader = Reader()
reader.read_journals()
