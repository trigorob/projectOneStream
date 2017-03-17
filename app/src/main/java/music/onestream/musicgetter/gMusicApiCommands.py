from __future__ import print_function, division, absolute_import, unicode_literals
from builtins import *  # noqa

from getpass import getpass

from gmusicapi import Mobileclient


def ask_for_credentials():
    api = Mobileclient()

    logged_in = False
    attempts = 0

    while not logged_in and attempts < 3:
        email = input('Email: ')
        password = getpass()

        logged_in = api.login(email, password, Mobileclient.FROM_MAC_ADDRESS)
        attempts += 1

    return api