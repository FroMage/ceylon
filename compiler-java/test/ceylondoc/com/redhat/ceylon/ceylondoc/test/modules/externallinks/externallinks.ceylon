/*
 * Copyright Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the authors tag. All rights reserved.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License version 2.
 * 
 * This particular file is subject to the "Classpath" exception as provided in the 
 * LICENSE file that accompanied this code.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License,
 * along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
import ceylon.collection { HashMap }
import ceylon.math.decimal { Decimal, zero }
import com.redhat.ceylon.ceylondoc.test.modules.dependency.b { B }
import com.redhat.ceylon.ceylondoc.test.modules.dependency.c { C }

shared B b() { throw; }
shared C c() { throw; }
shared HashMap<B, C> fceWithMap() { throw; }

doc "External links:
     
     - zero = [[zero]]
     - Decimal = [[Decimal]]
     - Decimal.divided = [[Decimal.divided]]
     - ceylon.math.whole::one = [[ceylon.math.whole::one]]
     - ceylon.math.whole::Whole = [[ceylon.math.whole::Whole]]
     - ceylon.math.whole::Whole.power = [[ceylon.math.whole::Whole.power]]"
shared void fceWithExternalLinksInDoc() {}