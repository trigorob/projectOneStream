from __future__ import print_function, division, absolute_import, unicode_literals
from builtins import *  # noqa

from getpass import getpass


from gmusicapi import Mobileclient
import sys
import gMusicApiCommands


"""
a = sys.argv
print(a[1])
print(a[2])
"""

## Args: argv

## pw: keystore 

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


api = ask_for_credentials()


print('Loading library...', end=' ')
library = api.get_all_songs()
print('done.')

print(len(library), 'tracks detected.')
print()

first_song = library[3]
print("The first song I see is '{}' by '{}'.".format(
    first_song['title'], first_song['artist']))
print("".format(first_song['albumArtist']))
## print("".format(first_song['artistId']))
## print("".format(first_song['albumId']))
print("".format(first_song['albumArtRef']))
print("".format(first_song['album']))

ret = api.get_stream_url(first_song['id'])
print(ret)

print("URL")