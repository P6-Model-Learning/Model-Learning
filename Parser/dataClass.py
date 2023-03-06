from dataclasses import dataclass
from datetime import datetime


@dataclass
class syslogAll:
    boards: list


@dataclass
class syslogBoard:
    name: str
    Entries: list
    pass


@dataclass
class syslogEntry:
    date: datetime
    message: str
    application: str
    classification: int

    def __init__(self, date: datetime, message: str, classification: int, application: str = ""):
        self.message = message
        self.date = date
        self.classification = classification
        self.application = application
