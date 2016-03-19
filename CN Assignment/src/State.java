public enum State{
		INIT{
			public int getValue(){
				return 1;
			}
		},
		SELECTING{
			public int getValue(){
				return 2;
			}
		},
		REQUESTING{
			public int getValue(){
				return 3;
			}
		},
		BOUND{
			public int getValue(){
				return 4;
			}
		},
		RENEWING{
			public int getValue(){
				return 5;
			}
		},
		REBINDING{
			public int getValue(){
				return 6;
			}
		},
		REBOOTING{
			public int getValue(){
				return 7;
			}
		},
		INITREBOOT{
			public int getValue(){
				return 8;
			}
		};

		public abstract int getValue();
	}